package com.devsoft.rgdi_store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.dto.UserMapper;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;	
	
	//validação para email único
	public boolean existsByEmail(String email) {
	    return repository.existsByEmail(email);
	}
	
	@Transactional(readOnly = true)
	public Page<UserDto> findAll(Pageable pageable) {
	    Page<UserEntity> result = repository.findAll(pageable);
	    // Usa o UserMapper para a conversão de UserEntity para UserDto
	    return result.map(UserMapper::toDto);
	}	
	
	@Transactional(readOnly = true)
	public Page<UserDto> findByName(String nome, Pageable pageable) {
	    Page<UserEntity> users;

	    if (nome == null || nome.isEmpty()) {
	        // Se o nome estiver vazio ou nulo, retorna todos os registros paginados
	        users = repository.findAll(pageable);
	    } else {
	        // Busca por nome com paginação
	        users = repository.findByNomeContainingIgnoreCase(nome, pageable);
	    }

	    // Mapeia as entidades para DTOs
	    return users.map(UserMapper::toPartialDto);
	}	
	
	@Transactional(readOnly = true)
	public UserDto findById(Long id) {		
		UserEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
		return UserMapper.toDto(entity); //retorna todos os campos do UserDto	
	}
	
	@Transactional
    public UserDto insert(UserDto dto) {
        try {
        	// Log para verificar o grupo recebido
            System.out.println("Grupo recebido no DTO: " + dto.getGrupo());
            
            UserEntity entity = UserMapper.toEntity(dto); // Converte DTO para entidade
            entity.setStatus(true); // Configuração adicional
            if (entity.getGrupo() == null) {
                entity.setGrupo(UserGroup.USER); // Define grupo padrão
            }
            entity = repository.save(entity); // Salva no banco
            return UserMapper.toDto(entity); // Retorna DTO convertido
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/insert [Campo Unique - Possivelmente e-mail duplicado]");
        }
    }
	
	@Transactional
    public UserDto update(Long id, UserDto dto) {
        try {
            UserEntity entity = repository.getReferenceById(id);
            UserMapper.updateEntityFromDto(dto, entity); // Atualiza a entidade com os dados do DTO
            entity = repository.save(entity);
            return UserMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }
	
	//exclusivo para o modal/edit
	@Transactional
    public UserDto updateModal(Long id, UserDto dto) {
        try {
            UserEntity entity = repository.getReferenceById(id);
            UserMapper.updateEntityFromDtoModal(dto, entity); // Atualiza a entidade com os dados do DTO - EXCLUSIVO DO MODAL
            entity = repository.save(entity);
            return UserMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }

	@Transactional
	public UserDto changeStatus(Long id) {
	    try {
	        UserEntity entity = repository.getReferenceById(id);
	        entity.setStatus(!entity.isStatus()); // Alterna o status
	        entity = repository.save(entity);
	        return UserMapper.toDto(entity);
	    } catch (EntityNotFoundException e) {
	        throw new ResourceNotFoundException("Recurso não encontrado - service/changeStatus");
	    }
	}
		
	
}
