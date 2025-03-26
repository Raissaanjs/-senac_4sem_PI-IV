package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.ProdutoImagens;

public class ProdutoImagensMapper {
    
	public static ProdutoImagensDto toDto(ProdutoImagens entity) {
        ProdutoImagensDto dto = new ProdutoImagensDto();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setProduto(entity.getProduto()); // Define o objeto ProdutoEntity
        return dto;
    }
	
	public static void updateEntityFromDto(ProdutoImagensDto dto, ProdutoImagens entity) {
        entity.setId(dto.getId());
		entity.setNome(dto.getNome());
    }
}