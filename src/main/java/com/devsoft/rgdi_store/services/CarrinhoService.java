package com.devsoft.rgdi_store.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

@Service
public class CarrinhoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    private List<ProdutoEntity> itens = new ArrayList<>();

    public void adicionarProduto(Long produtoId) {
        ProdutoEntity produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        itens.add(produto);
    }

    public List<ProdutoEntity> getItens() {
        return itens;
    }
}
