package com.devsoft.rgdi_store.entities;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_produto")
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;    
    private double preco;
    private int quantidade;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;    
    private int avaliacao;
    private boolean status;
    
    // 'orphanRemoval' Apagará no DB algum item que não conte na lista
    @OneToMany(mappedBy = "produto", orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<ProdutoImagens> produtoImagens;
    
    
    // Construtor padrão
    public ProdutoEntity() {
    	this.status = true; // Define o status como true por padrão
    }

    // Construtor com parâmetros
    public ProdutoEntity(Long id, String nome, double preco, int quantidade, String descricao, int avaliacao,
			boolean status, List<ProdutoImagens> produtoImagens) {
		super();
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
		this.descricao = descricao;
		this.avaliacao = avaliacao;
		this.status = status;
		this.produtoImagens = produtoImagens;
	}
    
    public ProdutoEntity(Long id, String nome, double preco) {
		this.id = id;
		this.nome = nome;
		this.preco = preco;
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

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(int avaliacao) {
		this.avaliacao = avaliacao;
	}
	
	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}	
	
	public List<ProdutoImagens> getProdutoImagens() {
		return produtoImagens;
	}

	public void setProdutoImagens(List<ProdutoImagens> pdi) {
		for(ProdutoImagens p: pdi) {
			p.setProduto(this);
		}
		this.produtoImagens = pdi;
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
		ProdutoEntity other = (ProdutoEntity) obj;
		return Objects.equals(id, other.id);
	}
}
