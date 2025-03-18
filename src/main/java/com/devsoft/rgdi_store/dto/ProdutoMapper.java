package com.devsoft.rgdi_store.dto;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

public class ProdutoMapper {

    // Converte Entity para Dto (mapeamento completo)
    public static ProdutoDto toDto(ProdutoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new ProdutoDto(
            entity.getId(),
            entity.getNome(),
            entity.getPreco(),
            entity.getQuantidade(),
            entity.getDescricao(),
            entity.getAvaliacao(),
            entity.isStatus()
        );
    }
    
    
    // Converte UserDto para UserEntity
    public static ProdutoEntity toEntity(ProdutoDto dto) {
        if (dto == null) {
            return null;
        }

        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setPreco(dto.getPreco());
        entity.setQuantidade(dto.getQuantidade());
        entity.setDescricao(dto.getDescricao());
        entity.setAvaliacao(dto.getAvaliacao());
        entity.setStatus(dto.isStatus());
        
        return entity;
    }
    
    public static void updateProductFromDto(ProdutoDto dto, ProdutoEntity entity, ProdutoRepository repository) {
        if (dto == null || entity == null) {
            return; // Caso o dto ou entidade sejam null, simplesmente retorna
        }

        // Atualiza o nome
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            entity.setNome(dto.getNome());
        }

        // Atualiza o preço
        if (dto.getPreco() >= 0) {
            entity.setPreco(dto.getPreco());
        }

        // Valida e atualiza a quantidade
        if (dto.getQuantidade() >= 0) {
            entity.setQuantidade(dto.getQuantidade());
        }

        // Valida e atualiza a descrição
        if (dto.getDescricao() != null && !dto.getDescricao().isEmpty()) {
            entity.setDescricao(dto.getDescricao());
        }

        // Valida e atualiza a avaliação
        if (dto.getAvaliacao() >= 1 && dto.getAvaliacao() <= 5) {
            entity.setAvaliacao(dto.getAvaliacao());
        }        
      
    }
}
