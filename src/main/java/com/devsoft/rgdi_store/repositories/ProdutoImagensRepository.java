package com.devsoft.rgdi_store.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.entities.ProdutoImagens;

public interface ProdutoImagensRepository extends JpaRepository<ProdutoImagens, Long> {

	Page<ProdutoImagens> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
	List<ProdutoImagens> findByProdutoId(Long produtoId);
	List<ProdutoImagens> findByProdutoIdAndPrincipalTrue(Long produtoId);
	boolean existsByProdutoAndPrincipal(ProdutoEntity produto, boolean principal);
	ProdutoImagens findByProdutoAndPrincipal(ProdutoEntity produto, boolean principal);
}