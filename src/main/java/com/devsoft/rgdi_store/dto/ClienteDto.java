package com.devsoft.rgdi_store.dto;

import java.sql.Date;
import java.util.List;

import com.devsoft.rgdi_store.entities.ClienteGenero;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.UserGroup;

public class ClienteDto {

	private Long id;    
    private String nome;
    private Date dataNascimento;
    private ClienteGenero genero;
    private String cpf;
    private String email;
    private String senha;
    private UserGroup grupo;    
    
    // Campo transitório (não será persistido)
    private transient String confirmasenha; //"transient" disponível no DTO apenas para validações temporárias    
    private List<EnderecoEntity> enderecos;
	
    
    public ClienteDto() {
	}
    
    public ClienteDto(Long id, String nome, Date dataNascimento, ClienteGenero genero, String cpf, String email,
			String senha, UserGroup grupo, String confirmasenha, List<EnderecoEntity> enderecos) {
		super();
		this.id = id;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.genero = genero;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.grupo = grupo;
		this.confirmasenha = confirmasenha;
		this.enderecos = enderecos;
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

	public String getConfirmasenha() {
		return confirmasenha;
	}

	public void setConfirmasenha(String confirmasenha) {
		this.confirmasenha = confirmasenha;
	}

	public List<EnderecoEntity> getEnderecos() {
		return enderecos;
	}

	public void setEnderecos(List<EnderecoEntity> enderecos) {
		this.enderecos = enderecos;
	}
	
	public UserGroup getGrupo() {
		return grupo;
	}
	
	public void setGrupo(UserGroup grupo) {
		this.grupo = grupo;
	}
    
    
}
