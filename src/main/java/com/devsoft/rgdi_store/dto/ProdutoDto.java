package com.devsoft.rgdi_store.dto;

import java.util.List;

public class ProductDto {
	
	private Long id;    
	private String nome;
    private double preco;
    private int quantidade;
    private String descricao;    
    private int avaliacao;
    private boolean status;
    private List<ProductImageDto> imagens;
    private List<String> imageUrls;
    
    public ProductDto() {}
    
    public ProductDto(Long id, String nome, double preco, int quantidade, String descricao, int avaliacao,
			boolean status, List<ProductImageDto> imagens, List<String> imageUrls) {
		super();
		this.id = id;
		this.nome = nome;
		this.preco = preco;
		this.quantidade = quantidade;
		this.descricao = descricao;
		this.avaliacao = avaliacao;
		this.status = status;
		this.imagens = imagens;
		this.imageUrls = imageUrls;
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

    public List<ProductImageDto> getImagens() {
        return imagens;
    }

    public void setImagens(List<ProductImageDto> imagens) {
        this.imagens = imagens;
    }

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
    
    
}
