package com.devsoft.rgdi_store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.EnderecoEntity;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {
    
}

