package com.devsoft.rgdi_store.entities;

public enum ClienteGenero {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
	OUTRO("Outro");

    private final String descricao;

    ClienteGenero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

