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
	
	// Busca uma lista de pedidos com parâmetro "cliente"
	List<PedidoEntity> findByCliente(ClienteEntity cliente); // Tipo Query: Spring Data JPA
	
	// Verifica se já existe o endereço no DB
	boolean existsByEndereco(EnderecoEntity endereco); // Tipo Query: Spring Data JPA

	// Busca todos os pedidos - com paginação
	Page<PedidoEntity> findAll(Pageable pageable); // Tipo Query: Spring Data JPA
	
	// Busca os pedidos baseado em intervalo de datas
	Page<PedidoEntity> findByDataCompraBetween(LocalDate dataInicio, LocalDate dataFim,
			Pageable pageable); // Tipo Query: Spring Data JPA
}
