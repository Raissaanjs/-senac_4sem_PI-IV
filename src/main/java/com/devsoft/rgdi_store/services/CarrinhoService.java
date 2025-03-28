package com.devsoft.rgdi_store.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CarrinhoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private HttpSession session;

    // Recupera o carrinho da sessão ou cria um novo se não existir
    private Map<Long, Integer> getCarrinho() {
        // Recupera o carrinho armazenado na sessão (Map de ID de produto para quantidade)
        @SuppressWarnings("unchecked")
        Map<Long, Integer> carrinho = (Map<Long, Integer>) session.getAttribute("carrinho");

        // Se não houver carrinho, cria um novo e armazena na sessão
        if (carrinho == null) {
            carrinho = new HashMap<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    // Adiciona um produto ao carrinho ou incrementa a quantidade se já estiver presente
    public void adicionarProduto(Long produtoId) {
        ProdutoEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Map<Long, Integer> carrinho = getCarrinho();

        // Verifica se o produto já está no carrinho
        if (carrinho.containsKey(produtoId)) {
            // Se o produto já está no carrinho, incrementa a quantidade
            carrinho.put(produtoId, carrinho.get(produtoId) + 1);
        } else {
            // Se o produto não estiver no carrinho, adiciona com a quantidade inicial de 1
            carrinho.put(produtoId, 1);  // Quantidade começa com 1
        }

        // Atualiza a sessão com os novos itens do carrinho
        session.setAttribute("carrinho", carrinho);
    }

    // Incrementa a quantidade de um produto no carrinho
    public void incrementarQuantidade(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();
        carrinho.put(produtoId, carrinho.getOrDefault(produtoId, 0) + 1);
        session.setAttribute("carrinho", carrinho);
    }

    // Decrementa a quantidade de um produto no carrinho
    public void decrementarQuantidade(Long produtoId) {
        Map<Long, Integer> carrinho = getCarrinho();
        int quantidadeAtual = carrinho.getOrDefault(produtoId, 0);
        
        if (quantidadeAtual > 1) {
            carrinho.put(produtoId, quantidadeAtual - 1);
        } else {
            carrinho.remove(produtoId);  // Remove o produto se a quantidade for 1
        }

        session.setAttribute("carrinho", carrinho);
    }

    // Retorna os itens no carrinho, com a quantidade de cada produto
    public List<ProdutoEntity> getItens() {
        Map<Long, Integer> carrinho = getCarrinho();
        List<ProdutoEntity> itensCarrinho = new ArrayList<>();

        // Para cada produto no carrinho, recuperamos o produto do banco de dados e associamos a quantidade
        for (Map.Entry<Long, Integer> entry : carrinho.entrySet()) {
            ProdutoEntity produto = produtoRepository.findById(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            produto.setQuantidade(entry.getValue());  // Define a quantidade do produto no carrinho
            itensCarrinho.add(produto);
        }

        return itensCarrinho;
    }

    // Limpa o carrinho da sessão
    public void limparCarrinho() {
        session.removeAttribute("carrinho");
    }
}



