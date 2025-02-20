package com.devsoft.rgdi_store.dto;

import org.springframework.beans.BeanUtils;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;

public class UserDto {

	private Long id;
	private String nome;
	private String cpf;
	private String email;
	private String senha;
	private String confirmaSenha;
	private String grupo;
	private UserGroup group;
	
	public UserGroup getGroup() {
		return group;
	}

	public void setGroup(UserGroup group) {
		this.group = group;
	}

	//Construtor para converter DTO para Entity
	public UserDto(UserEntity userEntity) {
		BeanUtils.copyProperties(userEntity, this);
	}

	public UserDto() {
	}

	public UserDto(Long id, String nome, String cpf, String email, String senha, String confirmaSenha, String grupo) {
		super();
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.confirmaSenha = confirmaSenha;
		this.grupo = grupo;
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

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
}
