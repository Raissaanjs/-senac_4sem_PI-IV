package com.devsoft.rgdi_store.entities;

import java.sql.Date;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cliente")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    private String nome;
    private Date dataNascimento;
    @Enumerated(EnumType.STRING) // Armazena o nome do enum (e.g., "MASCULINO", "FEMININO", etc)
    private ClienteGenero genero;
    @Column(unique = true)
    private String cpf;
    @Column(unique = true)
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING) // Armazena o nome do enum (e.g., "ADMIN", "ESTOQ")
    private UserGroup grupo;
    
    // 'orphanRemoval' Apagará no DB algum item que não conte na lista
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<EnderecoEntity> enderecos;
	
    
    public ClienteEntity() {
	}		

	public ClienteEntity(Long id, String nome, Date dataNascimento, ClienteGenero genero, String cpf, String email,
			String senha, UserGroup grupo) {
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.grupo = UserGroup.ROLE_CLIENT; // Define o grupo como ROLE_USER
	}

	public ClienteEntity(Long id, String nome, Date dataNascimento, ClienteGenero genero, String cpf, String email,
			String senha) {
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
	}
    
	
	
    //Demais Getters and Setters
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

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public ClienteGenero getGenero() {
		return genero;
	}

	public void setGenero(ClienteGenero genero) {
		this.genero = genero;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public UserGroup getGrupo() {
		return grupo;
	}

	public void setGrupo(UserGroup grupo) {
		this.grupo = grupo;
	}	

	public List<EnderecoEntity> getEnderecos() {
		return enderecos;
	}
	
	public void setEnderecos(List<EnderecoEntity> enderecos) {
		this.enderecos = enderecos;
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
		ClienteEntity other = (ClienteEntity) obj;
		return Objects.equals(id, other.id);
	}
}

