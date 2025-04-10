package com.devsoft.rgdi_store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long>{
	//validação para email único
	boolean existsByEmail(String email);
	
	//validação para email único
	boolean existsByCpf(String cpf);

}
