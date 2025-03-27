package com.devsoft.rgdi_store.entities;

import java.util.Objects;

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
	private String url;
	
	
	public ProdutoImagens() {
	}

	public ProdutoImagens(Long id, String nome, ProdutoEntity produto) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
	}	
	
	public ProdutoImagens(Long id, String nome, ProdutoEntity produto, String url) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
		this.url = url;
	}

	public ProdutoImagens(Long id, String nome, ProdutoEntity produto, boolean principal, String url) {
		super();
		this.id = id;
		this.nome = nome;
		this.produto = produto;
		this.principal = principal;
		this.url = url;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProdutoImagens other = (ProdutoImagens) obj;
		return Objects.equals(id, other.id);
	}
	
	
}
