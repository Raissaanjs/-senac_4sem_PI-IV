package com.devsoft.rgdi_store.mapper;

import com.devsoft.rgdi_store.dto.ProdutoDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.repositories.ProdutoRepository;

import java.math.BigDecimal;

public class ProdutoMapper {

    // Converte ProdutoEntity para ProdutoDto (mapeamento completo)
    public static ProdutoDto toDto(ProdutoEntity entity) {
        if (entity == null) {
            return null;
        }

        // Retorna uma nova instância de Dto
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

        // Cria uma nova instância de Entity
        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setPreco(dto.getPreco());
        entity.setQuantidade(dto.getQuantidade());
        entity.setDescricao(dto.getDescricao());
        entity.setAvaliacao(dto.getAvaliacao());
        entity.setStatus(dto.isStatus());

        // retorna a entidade
        return entity;
    }

    // Atualiza o produto
    public static void updateProductFromDto(ProdutoDto dto, ProdutoEntity entity, ProdutoRepository repository) {
        if (dto == null || entity == null) {
            return; // Caso o dto ou entidade sejam null, simplesmente retorna (Não faz nada)
        }

        // Atualiza o nome
        if (dto.getNome() != null && !dto.getNome().isEmpty()) {
            entity.setNome(dto.getNome());
        }

        // Atualiza o preço usando BigDecimal (verificação do valor)
        if (dto.getPreco() != null && dto.getPreco().compareTo(BigDecimal.ZERO) >= 0) {
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

