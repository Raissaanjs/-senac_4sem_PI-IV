package com.devsoft.rgdi_store.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.ItensPedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;
import com.devsoft.rgdi_store.entities.PedidoStatus;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ItensPedidoRepository;
import com.devsoft.rgdi_store.repositories.PedidoRepository;
import com.devsoft.rgdi_store.services.exceptions.All.PedidoNaoEncontradoException;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final ItensPedidoRepository itensPedidoRepository;
	private final CarrinhoService carrinhoService;
	
	public PedidoService(PedidoRepository pedidoRepository, ItensPedidoRepository itensPedidoRepository, 
						CarrinhoService carrinhoService) {
		this.pedidoRepository = pedidoRepository;
		this.itensPedidoRepository = itensPedidoRepository;
		this.carrinhoService = carrinhoService;
	}
	

	@Transactional(readOnly = true)
    public List<PedidoEntity> findAll() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PedidoEntity> findById(Long id) {
        return pedidoRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<PedidoEntity> findByCliente(ClienteEntity cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    @Transactional
    public PedidoEntity save(PedidoEntity pedido) {
        if (pedido.getStatus() == null) {
            pedido.setStatus(PedidoStatus.PAGO);
        }

        // Garante que cada item saiba qual pedido pertence
        if (pedido.getItensPedido() != null) {
            for (ItensPedidoEntity item : pedido.getItensPedido()) {
                item.setPedido(pedido);
            }
        }

        return pedidoRepository.save(pedido);
    }
    
    @Transactional
    public PedidoEntity criarPedidoComItens(PedidoEntity pedido) {
        List<ProdutoEntity> produtosCarrinho = carrinhoService.getItens();
        Map<Long, Integer> carrinho = carrinhoService.getCarrinho();

        // Proteção contra pedido com carrinho vazio ou sessão expirada
        if (produtosCarrinho == null || produtosCarrinho.isEmpty() || carrinho == null || carrinho.isEmpty()) {
            throw new IllegalStateException("Carrinho vazio. Não é possível finalizar o pedido.");
        }

        BigDecimal valorTotalPedido = BigDecimal.ZERO;
        List<ItensPedidoEntity> itensPedido = new ArrayList<>();

        for (ProdutoEntity produto : produtosCarrinho) {
            ItensPedidoEntity item = new ItensPedidoEntity();
            item.setProduto(produto);

            int quantidade = carrinho.getOrDefault(produto.getId(), 1); // Garante uma quantidade padrão
            item.setQtProduto(quantidade);

            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco());
            BigDecimal totalItem = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

            item.setVlUnitario(precoUnitario);
            item.setVlTotalPedido(totalItem);
            item.setPedido(pedido);

            itensPedido.add(item);
            valorTotalPedido = valorTotalPedido.add(totalItem);
        }

        // Soma o frete (se existir)
        if (pedido.getFrete() != null) {
            valorTotalPedido = valorTotalPedido.add(pedido.getFrete());
        }

        pedido.setValorTotal(valorTotalPedido);
        pedido.setItensPedido(itensPedido);

        PedidoEntity pedidoSalvo = pedidoRepository.save(pedido);

        // Salva os itens do pedido (se não tiver cascade)
        for (ItensPedidoEntity item : itensPedido) {
            itensPedidoRepository.save(item);
        }

        // Limpa carrinho da sessão
        carrinhoService.limparSessaoCompra();

        return pedidoSalvo;
    }

    @Transactional
    public PedidoEntity update(Long id, PedidoEntity pedidoAtualizado) {
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setTipo(pedidoAtualizado.getTipo());
            pedido.setFrete(pedidoAtualizado.getFrete());
            pedido.setValorTotal(pedidoAtualizado.getValorTotal());
            pedido.setStatus(pedidoAtualizado.getStatus());
            pedido.setCliente(pedidoAtualizado.getCliente());
            pedido.setEndereco(pedidoAtualizado.getEndereco());
            pedido.setItensPedido(pedidoAtualizado.getItensPedido());
            return pedidoRepository.save(pedido);
        }).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    @Transactional
    public void delete(Long id) {
        pedidoRepository.deleteById(id);
    }
    
    //Atualiza Status
    @Transactional
    public void updateStatus(Long id, PedidoStatus novoStatus) {
        PedidoEntity pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException("Pedido não encontrado com ID: " + id));
        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
    }

}
