package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	public List<UserDto> findAll(){
		List<UserEntity> result = repository.findAll();
		return result.stream().map(x -> new UserDto(x)).toList();
	}	
	
	@Transactional(readOnly = true)
	public UserDto findById(Long id) {
		Optional<UserEntity> result = repository.findById(id);
		UserEntity user = result.get();
		UserDto dto = new UserDto(user);
		return dto;
		
	}

}
