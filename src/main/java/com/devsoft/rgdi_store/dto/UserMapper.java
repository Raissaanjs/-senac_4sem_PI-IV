package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.UserEntity;

public class UserMapper {

    // Converte UserEntity para UserDto (mapeamento completo)
    public static UserDto toDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserDto(
            entity.getId(),
            entity.getNome(),
            entity.getCpf(),
            entity.getEmail(),
            entity.getSenha(),
            entity.getConfirmasenha(),
            entity.isStatus(),
            entity.getGrupo()
        );
    }

    // Converte UserEntity para UserDto (mapeamento para busca por nome)
    public static UserDto toPartialDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserDto(
            entity.getId(),
            entity.getNome(),
            entity.getEmail(),
            entity.isStatus(),
            entity.getGrupo() // Utiliza apenas os campos necessários
        );
    }
    
    // Converte UserEntity para UserDto (mapeamento do MODAL)
    public static UserDto toModalDto(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new UserDto(
            entity.getId(),
            entity.getNome(),
            entity.getCpf(),
            entity.getSenha(),
            entity.getConfirmasenha(),
            entity.getGrupo()
        );
    }

    // Converte UserDto para UserEntity
    public static UserEntity toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setCpf(dto.getCpf());
        entity.setEmail(dto.getEmail());
        entity.setSenha(dto.getSenha());
        entity.setConfirmasenha(dto.getConfirmasenha());
        entity.setStatus(dto.isStatus());
        entity.setGrupo(dto.getGrupo()); // Verifique se o grupo está sendo mapeado corretamente        
        return entity;
    }
    
    // Atualiza UserEntity com os dados de UserDto
    public static void updateEntityFromDto(UserDto dto, UserEntity entity) {
        if (dto == null || entity == null) {
            return; // Caso o dto ou entidade sejam null, simplesmente retorna
        }

        // Atualiza o nome se fornecido no DTO
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            entity.setNome(dto.getNome());
        }

        // Atualiza o CPF se fornecido no DTO
        if (dto.getCpf() != null && !dto.getCpf().isEmpty()) {
            entity.setCpf(dto.getCpf());
        }

        // Atualiza a senha apenas se fornecida no DTO
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            entity.setSenha(dto.getSenha());
        }

        // Atualiza o campo de confirmação da senha, se fornecido
        if (dto.getConfirmasenha() != null && !dto.getConfirmasenha().isEmpty()) {
            entity.setConfirmasenha(dto.getConfirmasenha());
        }

        // Atualiza o status APENAS se ele for diferente do valor atual
        if (dto.isStatus() != entity.isStatus()) {
            entity.setStatus(dto.isStatus());
        }

        // Atualiza o grupo apenas se fornecido no DTO
        if (dto.getGrupo() != null) {
            entity.setGrupo(dto.getGrupo());
        }
    }
    
    // Atualiza UserEntity com os dados de UserDto - Exclusivo MODAL
    public static void updateEntityFromDtoModal(UserDto dto, UserEntity entity) {
        if (dto == null || entity == null) {
            return; // Caso o dto ou entidade sejam null, simplesmente retorna
        }

        // Atualiza o nome se fornecido no DTO
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            entity.setNome(dto.getNome());
        }

        // Atualiza o CPF se fornecido no DTO
        if (dto.getCpf() != null && !dto.getCpf().isEmpty()) {
            entity.setCpf(dto.getCpf());
        }

        // Atualiza a senha apenas se fornecida no DTO
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            entity.setSenha(dto.getSenha());
        }

        // Atualiza o campo de confirmação da senha, se fornecido
        if (dto.getConfirmasenha() != null && !dto.getConfirmasenha().isEmpty()) {
            entity.setConfirmasenha(dto.getConfirmasenha());
        }
        
        // Atualiza o grupo apenas se fornecido no DTO
        if (dto.getGrupo() != null) {
            entity.setGrupo(dto.getGrupo());
        }
    }


}
