package com.devsoft.rgdi_store.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_produto")
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max=200, message="Tamanho máximo de 200 caracteres")
    private String nome;
    private double preco;
    private int quantidade;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    private int avaliacao;
    private boolean status;
    
    // 'orphanRemoval' Apagará no DB algum item que não conte na lista
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ProdutoImagens> produto_imagens;
    
    // Relacionamento com ItemPedido: Um produto pode ser parte de vários itens de pedido
    @OneToMany(mappedBy = "produto")
    private List<ItensPedidoEntity> itens_pedido;
    
    
    // Construtor padrão
    public ProdutoEntity() {
    	this.status = true; // Define o status como true por padrão
    }

    // Construtor com alguns parmetros
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

	public List<ProdutoImagens> getProduto_imagens() {
		return produto_imagens;
	}

	public void setProduto_imagens(List<ProdutoImagens> produto_imagens) {
		this.produto_imagens = produto_imagens;
	}

	public List<ItensPedidoEntity> getItens_pedido() {
		return itens_pedido;
	}

	public void setItens_pedido(List<ItensPedidoEntity> itens_pedido) {
		this.itens_pedido = itens_pedido;
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
