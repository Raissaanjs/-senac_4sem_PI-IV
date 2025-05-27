package com.devsoft.rgdi_store.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devsoft.rgdi_store.entities.ProdutoEntity;

import jakarta.transaction.Transactional;

@Repository
public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
	
	// Busca todos os produtos -  com paginação
    Page<ProdutoEntity> findAll(Pageable pageable); // Tipo Query: Spring Data JPA
    
    // Busca produto com parâmetro "nome" - com paginação - ignorando maiúscula ou minúscula
    Page<ProdutoEntity> findByNomeContainingIgnoreCase(String nome, Pageable pageable); // Tipo Query: Spring Data JPA

    // Busca uma lista de produtos com parâmetro "nome" - ignorando maiúscula ou minúscula
    List<ProdutoEntity> findByNomeContainingIgnoreCase(String nome); // Tipo Query: Spring Data JPA
    
    // Método que trata do estoque disponível
    // Tipo Query: JPQL (Java Persistence Query Language)
    @Modifying
    @Transactional
    @Query("UPDATE ProdutoEntity p SET p.quantidade = p.quantidade - :qtd WHERE p.id = :id AND p.quantidade >= :qtd")
    int descontarEstoqueSeDisponivel(@Param("id") Long id, @Param("qtd") int qtd);

}
