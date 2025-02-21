package com.devsoft.rgdi_store.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;

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
		Optional<UserEntity> result = repository.findById(id);
		UserEntity user = result.get();
		UserDto dto = new UserDto(user);
		return dto;		
	}
	
	@Transactional
	public UserDto insert(UserDto dto) {
		UserEntity entity = new UserEntity();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new UserDto(entity);
	}
	
	private void copyDtoToEntity(UserDto dto, UserEntity entity) {
		entity.setNome(dto.getNome());
		entity.setCpf(dto.getCpf());
		entity.setEmail(dto.getEmail());
		entity.setSenha(dto.getSenha());
		entity.setConfirmasenha(dto.getConfirmasenha());
		entity.setGrupo(dto.getGrupo());
	}

}
