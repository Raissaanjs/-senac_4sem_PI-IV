package com.devsoft.rgdi_store.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.ItensPedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoStatus;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.exceptions.all.CarrinhoVazioException;
import com.devsoft.rgdi_store.exceptions.all.EstoqueInsuficienteException;
import com.devsoft.rgdi_store.exceptions.all.PedidoNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.PedidoRepository;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final ProdutoRepository produtoRepository;
    
    public PedidoService(PedidoRepository pedidoRepository, 
                        CarrinhoService carrinhoService,
                        ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.carrinhoService = carrinhoService;
        this.produtoRepository = produtoRepository;
    }
    
    // Lista todos os pedidos
    @Transactional(readOnly = true)
    public List<PedidoEntity> findAll() {
        return pedidoRepository.findAll();
    }
    
    // Lista todos os pedidos co paginação
    @Transactional(readOnly = true)
    public Page<PedidoEntity> listarPedidosPaginados(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }
    
    // Busca por data - ADMIN
    @Transactional(readOnly = true)
    public Page<PedidoEntity> buscarPorIntervaloDeDatas(LocalDate inicio, LocalDate fim, Pageable pageable) {
        return pedidoRepository.findByDataCompraBetween(inicio, fim, pageable);
    }

    // Lista pedido por id
    @Transactional(readOnly = true)
    public Optional<PedidoEntity> findById(Long id) {
        return pedidoRepository.findById(id);
    }
    
    // Lista pedido por cliente
    @Transactional(readOnly = true)
    public List<PedidoEntity> findByCliente(ClienteEntity cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    @Transactional
    public PedidoEntity finalizarPedido(PedidoEntity pedido, List<ItemCarrinhoDTO> itensCarrinho) {
        // Proteção contra pedido com carrinho vazio
        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
        	//Se carrinho estiver vazio envia mensagem abaixo
            throw new CarrinhoVazioException("Carrinho vazio. Não é possível finalizar o pedido.");
        }

        BigDecimal valorTotalPedido = BigDecimal.ZERO; // Acumula o valor total dos itens do pedido
        
        // Cria uma lista vazia do tipo ItensPedidoEntity
        List<ItensPedidoEntity> itensPedido = new ArrayList<>();

        // Processa os itens do carrinho
        for (ItemCarrinhoDTO itemCarrinho : itensCarrinho) {
        	// Pega o produto do carrinho
            ProdutoEntity produto = itemCarrinho.getProduto();
            // Pega a quantidade do produto no carrinho
            int quantidade = itemCarrinho.getQuantidade();

            // Atualização segura diretamente no banco
            int atualizados = produtoRepository.descontarEstoqueSeDisponivel(produto.getId(), quantidade);
            if (atualizados == 0) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Carrega os dados atualizados do produto, se necessário
            ProdutoEntity produtoAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();

            // Criação do item de pedido
            ItensPedidoEntity item = new ItensPedidoEntity();
            item.setProduto(produtoAtualizado);
            item.setQtProduto(quantidade);
            item.setVlUnitario(produtoAtualizado.getPreco());            
            item.setVlTotalPedido(produtoAtualizado.getPreco().multiply(BigDecimal.valueOf(quantidade)));
            item.setPedido(pedido);  // Relaciona o item com o pedido

            // Adiciona o item à lista de itens do pedido
            itensPedido.add(item);

            // Soma o total do pedido
            valorTotalPedido = valorTotalPedido.add(item.getVlTotalPedido());
        }

        // Soma o frete ao valor total do pedido
        if (pedido.getFrete() != null) {
            valorTotalPedido = valorTotalPedido.add(pedido.getFrete());
        }

        // Atualiza o valor total do pedido
        pedido.setValorTotal(valorTotalPedido);

        // Associa os itens ao pedido (o CascadeType.ALL vai salvar os itens automaticamente)
        pedido.setItensPedido(itensPedido);

        // Salva o pedido no banco de dados
        PedidoEntity pedidoSalvo = pedidoRepository.save(pedido);

        // Limpa o carrinho da sessão
        carrinhoService.limparSessaoCompra();

        return pedidoSalvo;
    }
    
    // Atualiza o status do pedido
    @Transactional
    public void updateStatus(Long id, PedidoStatus novoStatus) {
    	// Busca pedido pelo ID
        PedidoEntity pedido = pedidoRepository.findById(id)
        	// Se não encontrar lança a exceção
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado com ID: " + id));
        // Seta o novo status
        pedido.setStatus(novoStatus);
        // salva o pedido com o novo status
        pedidoRepository.save(pedido);
    }
}

