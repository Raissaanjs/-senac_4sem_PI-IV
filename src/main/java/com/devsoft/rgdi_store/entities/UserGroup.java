package com.devsoft.rgdi_store.entities;

public enum UserGroup {
	ADMIN("admin"),
	ESTOQUISTA("estoquista"),	
    USER("user");

    private String group;

    UserGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

}
