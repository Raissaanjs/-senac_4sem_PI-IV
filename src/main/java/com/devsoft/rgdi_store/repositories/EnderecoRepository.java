package com.devsoft.rgdi_store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;

public interface EnderecoRepository extends JpaRepository<EnderecoEntity, Long> {
	
	// Busca uma lista de endereços com parâmetro "clienteId"
	List<EnderecoEntity> findAllByClienteId(Long clienteId); // Tipo Query: Spring Data JPA
	
	// Optional<EnderecoEntity> Evita null explícito
	// Busca endereco com parâmetros "cliente" e "EnderecoTipo: FATURAMENTO/ ENTREGA"
	Optional<EnderecoEntity> findByClienteAndTipo(ClienteEntity cliente, EnderecoTipo tipo); // Tipo Query: Spring Data JPA
	
}

