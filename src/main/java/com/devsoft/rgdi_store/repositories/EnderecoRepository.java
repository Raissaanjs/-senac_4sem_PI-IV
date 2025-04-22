package com.devsoft.rgdi_store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {
	Optional<EnderecoEntity> findByClienteAndTipo(ClienteEntity cliente, EnderecoTipo tipo);
    
}

