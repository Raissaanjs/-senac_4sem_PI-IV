package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

public class ProdutoImagensDto {

    private Long id;
    private String nome;
    private ProdutoEntity produto;
    

    public ProdutoImagensDto() {}  

    public ProdutoImagensDto(Long id, String nome, ProdutoEntity produto) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
	}

    // Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public ProdutoEntity getProduto() {
		return produto;
	}

	public void setProduto(ProdutoEntity produto) {
		this.produto = produto;
	}
	
   
}
