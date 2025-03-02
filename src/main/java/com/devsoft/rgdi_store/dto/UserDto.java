package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.validation.ValidCPF;
import com.devsoft.rgdi_store.validation.ValidEmail;
import com.devsoft.rgdi_store.validation.ValidationGroups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {
	
    private Long id;

    @NotBlank(message = "{nome.NotBlank}", groups ={ ValidationGroups.Create.class, ValidationGroups.Update.class })
    @Size(min = 3, max = 150, message = "{nome.Size}")
    private String nome;
   
    @ValidCPF(groups = { ValidationGroups.Create.class, ValidationGroups.Update.class })
    private String cpf;

    @ValidEmail(groups = ValidationGroups.Create.class)
    private String email;

    @NotBlank(message = "{senha.NotBlank}", groups = { ValidationGroups.Create.class, ValidationGroups.Update.class })
    private String senha;

    @NotBlank(message = "{confirmasenha.NotBlank}", groups = { ValidationGroups.Create.class, ValidationGroups.Update.class })
    private String confirmasenha;

    private UserGroup grupo = UserGroup.USER; //Define o grupo padrão como USER

    private boolean status = true;

    // Construtor padrão
    public UserDto() {
    }

    // Construtor com parâmetros
    public UserDto(Long id, String nome, String cpf, String email, String senha, String confirmasenha, UserGroup grupo, boolean status) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.confirmasenha = confirmasenha;
        this.grupo = grupo;
        this.status = status;
    }

    // Construtor para converter DTO para Entity
    public UserDto(UserEntity userEntity) {
        id = userEntity.getId();
        nome = userEntity.getNome();
        cpf = userEntity.getCpf();
        email = userEntity.getEmail();
        senha = userEntity.getSenha();
        confirmasenha = userEntity.getConfirmasenha();
        grupo = userEntity.getGrupo();
        status = userEntity.isStatus();
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

    public UserGroup getGrupo() {
        return grupo;
    }

    public void setGrupo(UserGroup grupo) {
        this.grupo = grupo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


	
}
