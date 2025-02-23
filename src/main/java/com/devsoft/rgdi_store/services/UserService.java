package com.devsoft.rgdi_store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.exceptions.DatabaseException;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Transactional(readOnly = true)
	public Page<UserDto> findAll(Pageable pageable){
		Page<UserEntity> result = repository.findAll(pageable);
		return result.map(x -> new UserDto(x));
	}	
	
	@Transactional(readOnly = true)
	public UserDto findById(Long id) {		
		UserEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - findById"));
		return new UserDto(entity);		
	}
	
	@Transactional
	public UserDto insert(UserDto dto) {		
		try {
			UserEntity entity = new UserEntity();
			
			entity.setNome(dto.getNome());
			entity.setCpf(dto.getCpf());
			entity.setEmail(dto.getEmail());
			entity.setSenha(dto.getSenha());
			entity.setConfirmasenha(dto.getConfirmasenha());
			entity.setGrupo(dto.getGrupo());
			entity.setStatus(true);
			
			entity = repository.save(entity);
		return new UserDto(entity);
		}catch(DataIntegrityViolationException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - insert [Campo Unique]");
		}
		/*
		catch(IllegalArgumentException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - insert [Senhas não conferem]");
		}*/
	}
	
	@Transactional
	public UserDto update(Long id, UserDto dto) {
		try {
			UserEntity entity = repository.getReferenceById(id);
			
			entity.setNome(dto.getNome());
			entity.setCpf(dto.getCpf());
			entity.setSenha(dto.getSenha());
			entity.setConfirmasenha(dto.getConfirmasenha());
			entity.setGrupo(dto.getGrupo());
			
			entity =repository.save(entity);
			return new UserDto(entity);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - update");
		}
		
		/*catch(IllegalArgumentException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - update [Senhas não conferem]");
		}*/
	}

	@Transactional(propagation = Propagation.SUPPORTS) //É acionado se esse método estiver no contexto de outra transação 
	public void delete(Long id) {		
		// verifica se o id existe
		if(!repository.existsById(id)) {			
			//Se não encontrar lança a excessão
			throw new ResourceNotFoundException("Recurso não encontrado - delete");
		}
		try {
			repository.deleteById(id);
		}catch(DataIntegrityViolationException e) {
			//Caso haja problema no modelo relacional - relacionamentos entre tables
			throw new DatabaseException("Falha de integridade referencial - delete");
		}
	}	
	
}
