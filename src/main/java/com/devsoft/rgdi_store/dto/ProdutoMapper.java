package com.devsoft.rgdi_store.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.repositories.ProductRepository;

public class ProductMapper {

    // Converte Entity para Dto (mapeamento completo)
    public static ProductDto toDto(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        // Converte a lista de ProductImageEntity para ProductImageDto
        List<ProductImageDto> imageDtos = entity.getImagens().stream()
            .map(image -> new ProductImageDto(image.getId(), image.getUrl(), image.getId()))
            .collect(Collectors.toList());

        // Extrai as URLs das imagens
        List<String> imageUrls = imageDtos.stream()
            .map(ProductImageDto::getUrl)
            .collect(Collectors.toList());

        return new ProductDto(
            entity.getId(),
            entity.getNome(),
            entity.getPreco(),
            entity.getQuantidade(),
            entity.getDescricao(),
            entity.getAvaliacao(),
            entity.isStatus(),
            imageDtos,
            imageUrls
        );
    }
    
    
    // Converte UserDto para UserEntity
    public static ProductEntity toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setPreco(dto.getPreco());
        entity.setQuantidade(dto.getQuantidade());
        entity.setDescricao(dto.getDescricao());
        entity.setAvaliacao(dto.getAvaliacao());
        entity.setStatus(dto.isStatus());

        // Converte a lista de ProductImageDto para ProductImageEntity
        List<ProductImageEntity> imageEntities = dto.getImagens().stream()
            .map(imageDto -> new ProductImageEntity(imageDto.getId(), imageDto.getUrl(), entity))
            .collect(Collectors.toList());

        entity.setImagens(imageEntities);

        return entity;
    }
    
    public static void updateProductFromDto(ProductDto dto, ProductEntity entity, ProductRepository repository) {
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
        
        // Atualiza a lista de imagens
        if (dto.getImagens() != null) {
            List<ProductImageEntity> imageEntities = dto.getImagens().stream()
                .map(imageDto -> new ProductImageEntity(imageDto.getId(), imageDto.getUrl(), entity))
                .collect(Collectors.toList());
            entity.setImagens(imageEntities);
        }
    }
}
