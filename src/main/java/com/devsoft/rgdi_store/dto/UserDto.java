package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.validation.ValidEmail;
import com.devsoft.rgdi_store.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {
    
    private Long id;

    //@NotBlank(message = "{nome.NotBlank}", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    @Size(min = 3, max = 150, message = "{nome.Size}", groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
    private String nome;
       
    @NotBlank(message = "{cpf.NotBlank}", groups = ValidationGroups.Create.class)
    private String cpf;

    @ValidEmail(groups = ValidationGroups.Create.class)
    @NotBlank(message = "{email.NotBlank}", groups = ValidationGroups.Create.class)
    private String email;

    @NotBlank(message = "{senha.NotBlank}", groups = ValidationGroups.Create.class)
    private String senha;

    // Campo transitório (não será persistido)
    @NotBlank(message = "{confirmasenha.NotBlank}", groups = ValidationGroups.Create.class)
    private transient String confirmasenha; //"transient" disponível no DTO apenas para validações temporárias

    private boolean status = true;
    private UserGroup grupo; // usando Enum

    // Construtor padrão
    public UserDto() {
    }

    // Construtor completo
    public UserDto(Long id, String nome, String cpf, String email, String senha, String confirmasenha, boolean status, UserGroup grupo) {
        super();
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.confirmasenha = confirmasenha;
        this.status = status;
        this.grupo = grupo;
    }

    // Construtor para busca por nome
    public UserDto(Long id, String nome, String email, boolean status, UserGroup grupo) {
        super();
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.status = status;
        this.grupo = grupo;
    }   

    // Construtor exclusivo para o modal/edit
    public UserDto(Long id, String nome, String cpf, String senha, String confirmasenha, UserGroup grupo) {
        super();
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.confirmasenha = confirmasenha;
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
}
