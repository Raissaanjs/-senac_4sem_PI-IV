package com.devsoft.rgdi_store.entities;

public enum EnderecoTipo {
    FATURAMENTO("Faturamento"),
    ENTREGA("Entrega");

    private final String descricao;

    EnderecoTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

