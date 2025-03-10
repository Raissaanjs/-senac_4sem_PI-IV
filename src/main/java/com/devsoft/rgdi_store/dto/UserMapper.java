package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.validation.user.UserValidationEditService;

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
            null,
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
            null,
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
        entity.setStatus(dto.isStatus());

        // Converte o grupo diretamente para a entidade
        if (dto.getGrupo() != null) {
            entity.setGrupo(dto.getGrupo()); // Sem necessidade de ajustes
        } else {
            entity.setGrupo(UserGroup.ROLE_USER); // Define grupo padrão
        }

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

        // Atualiza o status APENAS se ele for diferente do valor atual
        if (dto.isStatus() != entity.isStatus()) {
            entity.setStatus(dto.isStatus());
        }
        
        // Atualiza o grupo apenas se fornecido no DTO
        if (dto.getGrupo() != null) {
            // Usa diretamente o grupo do DTO
            entity.setGrupo(dto.getGrupo());
        }
    }
    
    // Atualiza UserEntity com os dados de UserDto - Exclusivo MODAL
    public static void updateEntityFromDtoModal(UserDto dto, UserEntity entity, UserRepository repository) {
        if (dto == null || entity == null) {
            return; // Caso o dto ou entidade sejam null, simplesmente retorna
        }

        // Valida e atualiza o nome
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            UserValidationEditService.validateAndUpdateName(dto.getNome(), entity);;
        }

        // Valida e atualiza o CPF
        if (dto.getCpf() != null && !dto.getCpf().isEmpty()) {
            UserValidationEditService.validateAndUpdateCpf(dto.getCpf(), entity);
        }

        // Valida e atualiza a senha (incluindo confirmação)
        if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            UserValidationEditService.validateAndUpdatePassword(dto.getSenha(), dto.getConfirmasenha(), entity);
        }

        // Atualiza o grupo diretamente (não precisa de validação complexa aqui, caso não haja)
        if (dto.getGrupo() != null) {
            entity.setGrupo(dto.getGrupo());
        }
    }


}