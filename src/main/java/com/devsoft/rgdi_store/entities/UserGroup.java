package com.devsoft.rgdi_store.entities;

public enum UserGroup {
	ADMIN("Administrador"),
	ESTOQ("Estoquista"),	
    USER("Usuário");

    private String grupo;

    UserGroup(String grupo){
        this.grupo = grupo;
    }

    public String getGroup(){
        return grupo;
    }

}
