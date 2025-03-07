package com.devsoft.rgdi_store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.authentication.PasswordUtils;
import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.dto.UserMapper;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.exceptions.EmailAlreadyExists;
import com.devsoft.rgdi_store.services.exceptions.InvalidCpfException;
import com.devsoft.rgdi_store.services.exceptions.PasswordConfirmationException;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.validation.CpfValidator;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;	
	
	@Autowired
	private PasswordUtils passwordUtils;
	
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
		// Valida o CPF antes de continuar
	    if (!CpfValidator.isValidCPF(dto.getCpf())) {
	        throw new InvalidCpfException("O CPF informado é inválido.");
	    }
		
		// Validação de regra de negócio antes de entrar no try-catch
	    if (repository.findByEmail(dto.getEmail()).isPresent()) {
	        throw new EmailAlreadyExists("O e-mail " + dto.getEmail() + " já está cadastrado no sistema.");
	    }
	    
	    // Verifica se a senha e a confirmação coincidem
	    if (!dto.getSenha().equals(dto.getConfirmasenha())) {
	    	throw new PasswordConfirmationException("As senhas não coincidem.");
	    }	    
	    

	    try {
	        // Log para verificar envio de senha
	        System.out.println("Senha enviada: " + dto.getSenha());

	        // Converte DTO para entidade
	        UserEntity entity = UserMapper.toEntity(dto);

	        // Validação de senha nula ou vazia
	        if (dto.getSenha() == null || dto.getSenha().isEmpty()) {
	            throw new IllegalArgumentException("Senha não foi enviada corretamente para o método insert.");
	        }

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
	
	//exclusivo para o modal/edit
	@Transactional
    public UserDto updateModal(Long id, UserDto dto) {
        try {
            UserEntity entity = repository.getReferenceById(id);
            UserMapper.updateEntityFromDtoModal(dto, entity); // Atualiza a entidade com os dados do DTO - EXCLUSIVO DO MODAL
            
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
