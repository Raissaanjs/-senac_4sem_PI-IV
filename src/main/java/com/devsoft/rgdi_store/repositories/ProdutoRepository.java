package com.devsoft.rgdi_store.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
	
	// Busca todos os produtos -  com paginação
    Page<ProdutoEntity> findAll(Pageable pageable); // Tipo Query: Spring Data JPA
    
    // Busca produto com parâmetro "nome" - com paginação - ignorando maiúscula ou minúscula
    Page<ProdutoEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable); // Tipo Query: Spring Data JPA

    // Busca uma lista de produtos com parâmetro "nome" - ignorando maiúscula ou minúscula
    List<ProdutoEntity> findByNomeContainingIgnoreCase(String nome); // Tipo Query: Spring Data JPA

}
