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
    @JoinColumn(name = "idProduto") //alterar o nome da coluna de de relacionamento no DB
    private ProdutoEntity produto;
	
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

	public ProdutoEntity getProduct() {
		return produto;
	}

	public void setProduct(ProdutoEntity produto) {
		this.produto = produto;
	}
	
	
}
