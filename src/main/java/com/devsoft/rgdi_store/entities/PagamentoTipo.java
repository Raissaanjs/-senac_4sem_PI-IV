package com.devsoft.rgdi_store.entities;

public enum PagamentoTipo {
    PIX("PIX"),
    CARTAOCREDITO("Cartao de Crédito"),
	BOLETO("Boleto Bancário");

    private final String descricao;

    PagamentoTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}

