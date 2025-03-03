package com.devsoft.rgdi_store.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.UserDto;
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
	public boolean existePorEmail(String email) {
	    return repository.existsByEmail(email);
	}
	
	@Transactional(readOnly = true)
	public Page<UserDto> findAll(Pageable pageable){
		Page<UserEntity> result = repository.findAll(pageable);
		return result.map(x -> new UserDto(x));
	}	
	
	@Transactional(readOnly = true)
	public UserDto findById(Long id) {		
		UserEntity entity = repository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Recurso não encontrado - service/findById [Verifique o id]"));
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
			
			// Define o grupo como USER se não estiver especificado
            if (dto.getGrupo() == null) {
                entity.setGrupo(UserGroup.USER);
            } else {
                entity.setGrupo(dto.getGrupo());
            }
			entity.setStatus(true);
			
			entity = repository.save(entity);
		return new UserDto(entity);
		}catch(DataIntegrityViolationException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - service/insert [Campo Unique - Possivelmente e-mail duplicado]");
		}
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
			entity.setStatus(dto.isStatus());
			entity =repository.save(entity);
			return new UserDto(entity);
		}catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado - service/update [verifique 'id'/ se está cadastrado]");
		}
	}

	@Transactional
	public UserDto changeStatus(Long id) {
	    try {
	        UserEntity entity = repository.getReferenceById(id);
	        entity.setStatus(!entity.isStatus()); // Alterna o status
	        entity = repository.save(entity);
	        return new UserDto(entity);
	    } catch (EntityNotFoundException e) {
	        throw new ResourceNotFoundException("Recurso não encontrado - service/changeStatus");
	    }
	}
	
	
	
}
