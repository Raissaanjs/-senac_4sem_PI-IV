package com.devsoft.rgdi_store.entities;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_endereco")
public class EnderecoEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;	
	private String cep;	
	private String logradouro;
	private Integer numero;
	private String complemento;
	private String bairro;
	private String localidade;
	private String uf;
	@Enumerated(EnumType.STRING) 
	@Column(nullable = false)
	private EnderecoTipo tipo;
	
	@ManyToOne
	@JoinColumn(name = "cliente_id", nullable = false) 
	private ClienteEntity cliente;
	
	// Relacionamento bidirecional: um endereço pode ter muitos pedidos
    @OneToMany(mappedBy = "endereco")
    private List<PedidoEntity> pedidos;
	
	
	public EnderecoEntity() {
	}	
		
	public EnderecoEntity(Long id, String cep, String logradouro, Integer numero, String complemento, String bairro,
			String localidade, String uf, EnderecoTipo tipo, ClienteEntity cliente) {
		this.id = id;
		this.cep = cep;
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
		this.tipo = tipo;
		this.cliente = cliente;
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public EnderecoTipo getTipo() {
		return tipo;
	}

	public void setTipo(EnderecoTipo tipo) {
		this.tipo = tipo;
	}

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}
	
	public List<PedidoEntity> getPedidos() {
		return pedidos;
	}

	public void setPedidos(List<PedidoEntity> pedidos) {
		this.pedidos = pedidos;
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
		EnderecoEntity other = (EnderecoEntity) obj;
		return Objects.equals(id, other.id);
	}
	
	// Usado para a lógica dos endereços
	public boolean mesmoConteudo(EnderecoEntity outro) {
	    if (outro == null) return false;

	    return Objects.equals(this.logradouro, outro.getLogradouro()) &&
	           Objects.equals(this.numero, outro.getNumero()) &&
	           Objects.equals(this.complemento, outro.getComplemento()) &&
	           Objects.equals(this.bairro, outro.getBairro()) &&
	           Objects.equals(this.localidade, outro.getLocalidade()) &&
	           Objects.equals(this.uf, outro.getUf()) &&
	           Objects.equals(this.cep, outro.getCep());
	}
	
}
