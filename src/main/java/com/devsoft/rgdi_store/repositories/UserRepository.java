package com.devsoft.rgdi_store.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	//validação para email único
	boolean existsByEmail(String email);	
	
    // Busca todos os registros com paginação
    Page<UserEntity> findAll(Pageable pageable);

    // Busca por nome contendo o termo (case-insensitive) com paginação
    Page<UserEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);


}
