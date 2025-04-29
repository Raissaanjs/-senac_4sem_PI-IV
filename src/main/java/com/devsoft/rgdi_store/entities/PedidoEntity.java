package com.devsoft.rgdi_store.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_pedido")
public class PedidoEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	private LocalDate dataCompra;
	@Enumerated(EnumType.STRING) 
	@Column(nullable = false, name = "tipo_pagamento")
	private PagamentoTipo tipo;
	@Column(name = "frete", precision = 10, scale = 2)
	private BigDecimal frete;
	@Column(name = "valor_total", precision = 10, scale = 2)
	private BigDecimal valorTotal;
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private PedidoStatus status;
	
	// Muitos pedidos pertencem a um cliente
	@ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;
	
	@ManyToOne
    @JoinColumn(name = "endereco_id", nullable = false)  // chave estrangeira
    private EnderecoEntity endereco;
		
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItensPedidoEntity> itensPedido;
	
	@PrePersist //permite inicializar ou modificar campos da entidade automaticamente
    public void prePersist() {
        this.dataCompra = LocalDate.now(); // Apenas a data atual
    }
	
	
	// Construtor padrão
	public PedidoEntity() {
		super();
	}


	//Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public LocalDate getDataCompra() {
		return dataCompra;
	}

	public void setDataCompra(LocalDate data_Compra) {
		this.dataCompra = data_Compra;
	}

	public PagamentoTipo getTipo() {
		return tipo;
	}

	public void setTipo(PagamentoTipo tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getFrete() {
		return frete;
	}

	public void setFrete(BigDecimal frete) {
		this.frete = frete;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public PedidoStatus getStatus() {
		return status;
	}

	public void setStatus(PedidoStatus status) {
		this.status = status;
	}

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}

	public EnderecoEntity getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoEntity endereco) {
		this.endereco = endereco;
	}

	public List<ItensPedidoEntity> getItensPedido() {
		return itensPedido;
	}

	public void setItensPedido(List<ItensPedidoEntity> itensPedido) {
		this.itensPedido = itensPedido;
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
		PedidoEntity other = (PedidoEntity) obj;
		return Objects.equals(id, other.id);
	}

}
