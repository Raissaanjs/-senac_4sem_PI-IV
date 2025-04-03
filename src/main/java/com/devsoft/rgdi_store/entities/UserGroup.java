package com.devsoft.rgdi_store.entities;

public enum UserGroup {
    ROLE_ADMIN("Administrador"),
    ROLE_ESTOQ("Estoquista"),
    ROLE_USER("Cliente");

    private final String descricao;

    UserGroup(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    // Método para encontrar o grupo com base no role (opcional)
    public static UserGroup fromGrupo(String grupo) {
        for (UserGroup userGroup : UserGroup.values()) {
            if (userGroup.name().equalsIgnoreCase(grupo)) { // Compara com o nome do enum
                return userGroup;
            }
        }
        throw new IllegalArgumentException("Grupo inválido: " + grupo);
    }
}

