package com.devsoft.rgdi_store.entities;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.devsoft.rgdi_store.dto.UserDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_usuario")
public class UserEntity implements UserDetails{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String nome;
	@Column(nullable = false)
	private String cpf;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	private String senha;
	@Column(nullable = false)
	private String confirmaSenha;
	private UserGroup group;
	
	//Construtor para converter Entity para DTO
	public UserEntity(UserDto userDto) {
		BeanUtils.copyProperties(userDto, this);		
	}
	
	
	public UserEntity() {
	}
		
	public UserEntity(String nome, String cpf, String email, String senha, String confirmaSenha, UserGroup group) {
		super();
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.confirmaSenha = confirmaSenha;
		this.group = group;
	}

	public UserEntity(Long id, String nome, String cpf, String email, String senha, String confirmaSenha,
			UserGroup group) {
		super();
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.email = email;
		this.senha = senha;
		this.confirmaSenha = confirmaSenha;
		this.group = group;
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
		
	public UserGroup getGroup() {
		return group;
	}

	public void setGroup(UserGroup group) {
		this.group = group;
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
	

}
