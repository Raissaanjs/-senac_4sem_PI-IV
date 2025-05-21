package com.devsoft.rgdi_store.repositories;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.devsoft.rgdi_store.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>{
	// Usado na validação para email único
	boolean existsByEmail(String email); // Tipo Query: Spring Data JPA
	
    // Busca todos os usuários - com paginação
    Page<UserEntity> findAll(Pageable pageable); // Tipo Query: Spring Data JPA

    // Busca usuário com parâmetro "nome" - com paginação - ignorando maiúscula ou minúscula
    Page<UserEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable); // Tipo Query: Spring Data JPA

    // Busca usuário com parâmetro "email"
    Optional<UserEntity> findByEmail(String email); // Tipo Query: Spring Data JPA
}
