package com.devsoft.rgdi_store.entities;

import java.util.Objects;

import com.devsoft.rgdi_store.dto.UserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_usuario")
public class UserEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	@Column(length=11)
	private String cpf;
	@Column(unique = true)
	private String email;
	private String senha;
	private String confirmasenha;
	private UserGroup grupo;
	
	//Construtor para converter Entity para DTO
	public UserEntity(UserDto userDto) {
		id = userDto.getId();
		nome = userDto.getNome();
		cpf = userDto.getCpf() ;
		email = userDto.getEmail();
		senha = userDto.getSenha();
		confirmasenha = userDto.getConfirmasenha();
		grupo = userDto.getGrupo();
	}
	
	
	public UserEntity() {
	}

	public UserEntity(Long id, String nome, String cpf, String email, String senha, String confirmasenha,
			UserGroup grupo) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.confirmasenha = confirmasenha;
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
			
	public String getConfirmasenha() {
		return confirmasenha;
	}

	public void setConfirmasenha(String confirmasenha) {
		this.confirmasenha = confirmasenha;
	}

	public UserGroup getGrupo() {
		return grupo;
	}

	public void setGrupo(UserGroup grupo) {
		this.grupo = grupo;
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
		UserEntity other = (UserEntity) obj;
		return Objects.equals(id, other.id);
	}

	
/*
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Se for ADMIN, terá autorização ADMIN e USER
    	if(this.group == UserGroup.ADMIN) {
        	return List.of(new SimpleGrantedAuthority("GROUP_ADMIN"), new SimpleGrantedAuthority("GROUP_USER"));
        }else if(this.group == UserGroup.ESTOQUISTA) {
        	return List.of(new SimpleGrantedAuthority("GROUP_ESTOQUISTA"), new SimpleGrantedAuthority("GROUP_USER"));
        }else {
        	return List.of(new SimpleGrantedAuthority("GROUP_USER"));}
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
*/	

}
