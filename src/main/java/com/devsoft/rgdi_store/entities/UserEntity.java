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
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String cpf;
    @Column(unique = true)
    private String email;
    private String senha;
    private String confirmasenha;
    private boolean status;
    private UserGroup grupo;    

    // Construtor para converter Entity para DTO
    public UserEntity(UserDto userDto) {
        id = userDto.getId();
        nome = userDto.getNome();
        cpf = userDto.getCpf();
        email = userDto.getEmail();
        senha = userDto.getSenha();
        confirmasenha = userDto.getConfirmasenha();        
        status = userDto.isStatus();
        grupo = userDto.getGrupo();
    }

    // Construtor padrão
    public UserEntity() {
    }

    // Construtor com parâmetros
    public UserEntity(Long id, String nome, String cpf, String email, String senha, String confirmasenha, boolean status, UserGroup grupo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.confirmasenha = confirmasenha;
        this.status = status;
        this.grupo = grupo;
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
    
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

}

