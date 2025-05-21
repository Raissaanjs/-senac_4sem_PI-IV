package com.devsoft.rgdi_store.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;

public interface ProdutoImagensRepository extends JpaRepository<ProdutoImagens, Long> {

	// Busca imagens de produtos com parâmetro "nome" - com paginação - ignorando maiúscula ou minúscula
	Page<ProdutoImagens> findByNomeContainingIgnoreCase(String nome, Pageable pageable);  // Tipo Query: Spring Data JPA
	
	// Busca uma lista de imagens de produtos com parâmetro "produtoId"
	List<ProdutoImagens> findByProdutoId(Long produtoId); // Tipo Query: Spring Data JPA
	
	// Busca uma Lista de imagens de produtos que são marcadas como "principal", com parâmetro "produtoId"
	List<ProdutoImagens> findByProdutoIdAndPrincipalTrue(Long produtoId); // Tipo Query: Spring Data JPA
	
	// Verifica se existe imagem associada a um "produto" onde o "principal" seja true ou false
	boolean existsByProdutoAndPrincipal(ProdutoEntity produto, boolean principal); // Tipo Query: Spring Data JPA
	
	// Busca uma única imagem associada a um "produto", onde o "principal" seja true ou false
	ProdutoImagens findByProdutoAndPrincipal(ProdutoEntity produto, boolean principal); // Tipo Query: Spring Data JPA
}