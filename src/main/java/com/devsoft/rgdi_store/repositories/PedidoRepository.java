package com.devsoft.rgdi_store.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.PedidoEntity;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long>{
	List<PedidoEntity> findByCliente(ClienteEntity cliente);
	
	boolean existsByEndereco(EnderecoEntity endereco);

	Page<PedidoEntity> findAll(Pageable pageable);
	
	Page<PedidoEntity> findByDataCompraBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);
}
