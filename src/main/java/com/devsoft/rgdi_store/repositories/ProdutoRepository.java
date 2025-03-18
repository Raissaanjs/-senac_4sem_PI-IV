package com.devsoft.rgdi_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
	
	// Busca todos os registros com paginação
    Page<ProdutoEntity> findAll(Pageable pageable);
    
    // Busca por nome contendo o termo (case-insensitive) com paginação
    Page<ProdutoEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
