package com.devsoft.rgdi_store.entities;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_itens_pedido")
public class ItensPedidoEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column(name = "qt_produto")
	private Integer qtProduto;
	@Column(name = "vl_unitario", precision = 10, scale = 2, nullable = false)
	private BigDecimal vlUnitario;
	@Column(name = "vl_total_pedido", precision = 10, scale = 2, nullable = false)
	private BigDecimal vlTotalPedido;
	
	@ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;
	
	@ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

	
	public ItensPedidoEntity() {
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getQtProduto() {
		return qtProduto;
	}

	public void setQtProduto(Integer qtProduto) {
		this.qtProduto = qtProduto;
	}

	public BigDecimal getVlUnitario() {
		return vlUnitario;
	}

	public void setVlUnitario(BigDecimal vlUnitario) {
		this.vlUnitario = vlUnitario;
	}

	public BigDecimal getVlTotalPedido() {
		return vlTotalPedido;
	}

	public void setVlTotalPedido(BigDecimal vlTotalPedido) {
		this.vlTotalPedido = vlTotalPedido;
	}

	public ProdutoEntity getProduto() {
		return produto;
	}

	public void setProduto(ProdutoEntity produto) {
		this.produto = produto;
	}

	public PedidoEntity getPedido() {
		return pedido;
	}

	public void setPedido(PedidoEntity pedido) {
		this.pedido = pedido;
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
		ItensPedidoEntity other = (ItensPedidoEntity) obj;
		return Objects.equals(id, other.id);
	}
	

}
