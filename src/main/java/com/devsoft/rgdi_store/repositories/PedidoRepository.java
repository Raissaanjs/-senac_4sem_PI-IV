package com.devsoft.rgdi_store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long>{
	List<PedidoEntity> findByCliente(ClienteEntity cliente);
	
}
