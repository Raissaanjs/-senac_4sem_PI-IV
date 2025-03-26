package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

public class ProdutoImagensDto {

    private Long id;
    private String nome;
    private ProdutoEntity produto;
    private boolean principal;
    private String url;
    
    public ProdutoImagensDto() {}  

	public ProdutoImagensDto(Long id, String nome, ProdutoEntity produto, boolean principal) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
		this.principal = principal;
	}	

	public ProdutoImagensDto(String nome, boolean principal) {
		this.nome = nome;
		this.principal = principal;
	}	

	public ProdutoImagensDto(boolean principal, String url) {
		super();
		this.principal = principal;
		this.url = url;
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

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
		
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
