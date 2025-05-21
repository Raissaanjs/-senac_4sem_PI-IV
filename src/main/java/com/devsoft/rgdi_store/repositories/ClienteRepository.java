package com.devsoft.rgdi_store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devsoft.rgdi_store.entities.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long>{
	// Usado na validação para "email" único
	boolean existsByEmail(String email); // Tipo Query: Spring Data JPA
	
	// Usado na validação para "cpf" único
	boolean existsByCpf(String cpf); // Tipo Query: Spring Data JPA
	
	// Busca cliente com parâmetro "email"
    Optional<ClienteEntity> findByEmail(String email); // Tipo Query: Spring Data JPA
    
    // Busca o endereço com parâmetro "id" do cliente
    @Query("SELECT c FROM ClienteEntity c LEFT JOIN FETCH c.enderecos WHERE c.id = :id") // Tipo Query: JPQL
    Optional<ClienteEntity> findByIdComEnderecos(@Param("id") Long id);
}
