package com.devsoft.rgdi_store.dto;

import java.math.BigDecimal;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

public class ItemCarrinhoDTO {
    private ProdutoEntity produto;
    private int quantidade;

    public ItemCarrinhoDTO() {}

    public ItemCarrinhoDTO(ProdutoEntity produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public ProdutoEntity getProduto() {
        return produto;
    }

    public void setProduto(ProdutoEntity produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // Método que calcula o valor total do item
    public BigDecimal getValorTotal() {
        if (produto == null || produto.getPreco() == null) {
            return BigDecimal.ZERO;
        }
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }
}
