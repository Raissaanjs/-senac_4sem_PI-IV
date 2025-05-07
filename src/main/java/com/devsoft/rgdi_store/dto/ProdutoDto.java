package com.devsoft.rgdi_store.dto;

import java.math.BigDecimal;

public class ProdutoDto {
	
	private Long id;    
	private String nome;
    private BigDecimal preco;
    private int quantidade;
    private String descricao;    
    private int avaliacao;
    private boolean status;
    
    public ProdutoDto() {}
    
    public ProdutoDto(Long id, String nome, BigDecimal preco, int quantidade, String descricao, int avaliacao,
			boolean status) {
		super();
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
		this.descricao = descricao;
		this.avaliacao = avaliacao;
		this.status = status;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
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
    
    
}
