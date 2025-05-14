package com.devsoft.rgdi_store.services;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.authentication.PasswordUtils;
import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.dto.UserMapper;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.validation.user.UserValidationSaveService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {	
	
	private final UserRepository repository;	
	private final PasswordUtils passwordUtils;

    public UserService(PasswordUtils passwordUtils,
    				   UserRepository repository) {
        this.passwordUtils = passwordUtils;
        this.repository = repository;
    }	
	
	//validação para email único
	public boolean existsByEmail(String email) {
	    return repository.existsByEmail(email);
	}	
	
	//Mostra todos os registros
	@Transactional(readOnly = true)
	public Page<UserDto> findAll(Pageable pageable) {
		if (pageable == null) {
	        pageable = PageRequest.of(0, 5); // Página 0 com 10 itens por padrão
	    }
	    Page<UserEntity> result = repository.findAll(pageable);
	    // Usa o UserMapper para a conversão de UserEntity para UserDto
	    return result.map(UserMapper::toDto);
	}	

	//Busca por nome com paginação
	@Transactional(readOnly = true)
	public Page<UserDto> findByName(String nome, Pageable pageable) {
	    // Define a página padrão caso 'pageable' seja nulo
	    if (pageable == null) {
	        pageable = PageRequest.of(0, 5);
	    }

	    // Verifica se o nome está vazio ou nulo e decide qual consulta executar
	    Page<UserEntity> result;
	    if (nome == null || nome.trim().isEmpty()) {
	        // Se o nome estiver vazio, retorna todos os usuários paginados
	        result = repository.findAll(pageable);
	    } else {
	        // Se houver um nome para buscar, realiza a consulta com filtro
	        result = repository.findByNomeContainingIgnoreCase(nome, pageable);
	    }

	    // Converte o resultado em UserDto usando o UserMapper
	    return result.map(UserMapper::toDto);
	}

	// Busca por id
	@Transactional(readOnly = true)
	public UserDto findById(Long id) {		
		UserEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
		return UserMapper.toDto(entity); //retorna todos os campos do UserDto	
	}
	
	//Save
	@Transactional
	public UserDto insert(UserDto dto) {
		UserValidationSaveService.validateUser(dto, repository);		

	    try {      

	        // Converte DTO para entidade
	        UserEntity entity = UserMapper.toEntity(dto);	        

	        // Adiciona a criptografia à senha
	        entity.setSenha(passwordUtils.encrypt(dto.getSenha()));

	        // Define o Status como ativo
	        entity.setStatus(true);

	        // Salva no banco
	        entity = repository.save(entity);

	        // Retorna DTO convertido
	        return UserMapper.toDto(entity);
	    } catch (DataIntegrityViolationException e) {
	        throw new ResourceNotFoundException("Erro de integridade referencial - verifique relacionamentos no banco.");
	    }
	}
	
	//Atualização full
	@Transactional
    public UserDto update(Long id, UserDto dto) {
        try {
            UserEntity entity = repository.getReferenceById(id);
            UserMapper.updateEntityFromDto(dto, entity); // Atualiza a entidade com os dados do DTO
            
            // Criptografa a senha caso tenha sido fornecida
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            	entity.setSenha(passwordUtils.encrypt(dto.getSenha())); // Criptografa a senha
            }
            
            entity = repository.save(entity);
            
            return UserMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }
	
	//Atualização - exclusivo para o modal/edit
	@Transactional
    public UserDto updateModal(Long id, UserDto dto) {
        try {
            UserEntity entity = repository.getReferenceById(id);
            UserMapper.updateEntityFromDtoModal(dto, entity, repository); // Atualiza a entidade com os dados do DTO - EXCLUSIVO DO MODAL
            
            // Criptografa a senha caso tenha sido fornecida
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
            	entity.setSenha(passwordUtils.encrypt(dto.getSenha())); // Criptografa a senha
            }
            
            entity = repository.save(entity);
            
            return UserMapper.toDto(entity); // Retorna DTO convertido
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
        }
    }

	//Altera o status
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
