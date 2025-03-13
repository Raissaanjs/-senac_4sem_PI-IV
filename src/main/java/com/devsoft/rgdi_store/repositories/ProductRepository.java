package com.devsoft.rgdi_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsoft.rgdi_store.entities.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	
	// Busca todos os registros com paginação
    Page<ProductEntity> findAll(Pageable pageable);
    
    // Busca por nome contendo o termo (case-insensitive) com paginação
    Page<ProductEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
