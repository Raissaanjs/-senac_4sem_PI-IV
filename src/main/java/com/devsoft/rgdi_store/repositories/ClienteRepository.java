package com.devsoft.rgdi_store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devsoft.rgdi_store.entities.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long>{
	//validação para email único
	boolean existsByEmail(String email);
	
	//validação para email único
	boolean existsByCpf(String cpf);
	
	// Busca por email
    Optional<ClienteEntity> findByEmail(String email);
    
    @Query("SELECT c FROM ClienteEntity c LEFT JOIN FETCH c.enderecos WHERE c.id = :id")
    Optional<ClienteEntity> findByIdComEnderecos(@Param("id") Long id);
}
