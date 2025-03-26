package com.devsoft.rgdi_store.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "produto_imagens")
public class ProdutoImagens {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	private String nome;
	
	@ManyToOne
    @JoinColumn(name = "idProduto") //alterar o nome da coluna de relacionamento no DB
	//@JsonIgnore // Evita que entre em loop
    private ProdutoEntity produto;
	private boolean principal;
	
	
	public ProdutoImagens() {
	}

	public ProdutoImagens(Long id, String nome, ProdutoEntity produto) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
	}


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
	
	
}
