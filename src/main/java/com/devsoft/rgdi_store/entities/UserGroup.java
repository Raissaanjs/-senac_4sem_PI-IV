package com.devsoft.rgdi_store.entities;

public enum UserGroup {
	ADMIN("admin"),
	ESTOQUE("estoquista"),	
    USER("user");

    private String grupo;

    UserGroup(String grupo){
        this.grupo = grupo;
    }

    public String getGroup(){
        return grupo;
    }

}
