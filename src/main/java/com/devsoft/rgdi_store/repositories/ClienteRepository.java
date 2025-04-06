package com.devsoft.rgdi_store.repositories;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long>{
	//validação para email único
	boolean existsByEmail(String email);
	
	//validação para email único
	boolean existsByCpf(String cpf);
	
    // Busca todos os registros com paginação
    Page<ClienteEntity> findAll(Pageable pageable);

    // Busca por nome contendo o termo (case-insensitive) com paginação
    Page<ClienteEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Busca por email
    Optional<ClienteEntity> findByEmail(String email);


}
