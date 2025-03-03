package com.devsoft.rgdi_store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	//validação para email único
	boolean existsByEmail(String email);
}
