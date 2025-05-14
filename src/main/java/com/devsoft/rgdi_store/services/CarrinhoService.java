package com.devsoft.rgdi_store.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.dto.ItemCarrinhoDTO;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

@Service
public class CarrinhoService {

    private final ProdutoRepository produtoRepository;
    private final HttpSession session;

    public CarrinhoService(ProdutoRepository produtoRepository,
    						HttpSession session) {
        this.produtoRepository = produtoRepository;
        this.session = session;
    }	

    // Recupera o carrinho da sessão ou cria um novo se não existir
    public Map<Long, Integer> getCarrinho() {
        @SuppressWarnings("unchecked")
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute("carrinho");

        if (carrinho == null) {
            carrinho = new HashMap<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    // Método para contar o número de produtos únicos no carrinho
    public int getQuantidadeTotalItens() {
        Map<Long, Integer> carrinho = getCarrinho();
        return carrinho.size();
    }

    // Adiciona um produto ao carrinho ou incrementa a quantidade se já estiver presente
    public void adicionarProduto(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();

        if (carrinho.containsKey(produtoId)) {
            carrinho.put(produtoId, carrinho.get(produtoId) + 1);
        } else {
            carrinho.put(produtoId, 1);
        }

        session.setAttribute("carrinho", carrinho);
    }

    // Botão(+) que incrementa a quantidade de um produto no carrinho
    public void incrementarQuantidade(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();
        carrinho.put(produtoId, carrinho.getOrDefault(produtoId, 0) + 1);
        session.setAttribute("carrinho", carrinho);
    }

    // Botão(-) que decrementa a quantidade de um produto no carrinho
    public void decrementarQuantidade(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();
        int quantidadeAtual = carrinho.getOrDefault(produtoId, 0);

        if (quantidadeAtual > 1) {
            carrinho.put(produtoId, quantidadeAtual - 1);
        } else {
            carrinho.remove(produtoId);
        }

        session.setAttribute("carrinho", carrinho);
    }

    // Retorna os itens no carrinho, com a quantidade de cada produto
    public List<ItemCarrinhoDTO> getItens() {
        Map<Long, Integer> carrinho = getCarrinho();
        List<ItemCarrinhoDTO> itensCarrinho = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : carrinho.entrySet()) {
            ProdutoEntity produto = produtoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            
            int quantidade = entry.getValue();
            itensCarrinho.add(new ItemCarrinhoDTO(produto, quantidade));
        }

        return itensCarrinho;
    }


    // Limpa o carrinho da sessão
    public void limparCarrinho() {
        session.removeAttribute("carrinho");
    }

    // Método para remover um produto do carrinho
    public void removerProduto(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();
        carrinho.remove(produtoId);

        if (carrinho.isEmpty()) {
            zerarFrete();  // Zera o valor do frete se o carrinho estiver vazio
        }

        session.setAttribute("carrinho", carrinho);
    }

    private void zerarFrete() {
        session.setAttribute("frete", BigDecimal.ZERO);  // Altere para BigDecimal
    }

    public void limparSessaoCompra() {
        session.removeAttribute("carrinho");
        session.removeAttribute("frete");
    }

    // Calcula o valor total do carrinho
    public BigDecimal calcularTotalCarrinho() {
        List<ItemCarrinhoDTO> itensCarrinho = getItens();
        return itensCarrinho.stream()
                .map(item -> item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Soma os totais
    }
}


