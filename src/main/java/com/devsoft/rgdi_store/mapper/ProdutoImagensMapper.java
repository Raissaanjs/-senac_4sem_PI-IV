package com.devsoft.rgdi_store.mapper;

import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoImagens;

public class ProdutoImagensMapper {
    
	// Converte uma entidade ProdutoImagens em um DTO ProdutoImagensDto
	public static ProdutoImagensDto toDto(ProdutoImagens entity) {
		// Cria uma nova instância do DTO
		ProdutoImagensDto dto = new ProdutoImagensDto();
		
		// Copia os dados da entidade para o DTO
		dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setProduto(entity.getProduto()); // Define o objeto ProdutoEntity
        dto.setUrl(entity.getUrl());
        
        // Retorna o DTO preenchido
        return dto;
    }
	
	// Atualiza a imagem do produto
	public static void updateEntityFromDto(ProdutoImagensDto dto, ProdutoImagens entity) {
		// Atualiza apenas o ID e o nome da imagem
        entity.setId(dto.getId());
		entity.setNome(dto.getNome());
    }
}