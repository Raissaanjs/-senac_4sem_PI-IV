package com.devsoft.rgdi_store.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ProductImageEntity;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
    // Busca todos os registros com paginação
    Page<ProductImageEntity> findAll(Pageable pageable);
}