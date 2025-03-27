package com.devsoft.rgdi_store.services;

import java.util.ArrayList;
import java.util.List;

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
    private List<ProdutoEntity> getCarrinho() {
        // Recupera o carrinho armazenado na sessão
        @SuppressWarnings("unchecked")
        List<ProdutoEntity> carrinho = (List<ProdutoEntity>) session.getAttribute("carrinho");
        
        // Se não houver carrinho, cria um novo e armazena na sessão
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    // Adiciona um produto ao carrinho
    public void adicionarProduto(Long produtoId) {
        ProdutoEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<ProdutoEntity> carrinho = getCarrinho();
        carrinho.add(produto);  // Adiciona o produto no carrinho

        // Atualiza a sessão com os novos itens do carrinho
        session.setAttribute("carrinho", carrinho);
    }

    // Retorna os itens no carrinho
    public List<ProdutoEntity> getItens() {
        return getCarrinho();
    }

    // Limpa o carrinho da sessão
    public void limparCarrinho() {
        session.removeAttribute("carrinho");
    }
}


