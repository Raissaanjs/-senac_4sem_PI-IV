package com.devsoft.rgdi_store.entities;

public enum ClienteGenero {
    HOMEM("Homem"),
    MULHER("Mulher"),
	OUTRO("Outro");

    private final String descricao;

    ClienteGenero(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

