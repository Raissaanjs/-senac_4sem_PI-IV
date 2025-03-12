package com.devsoft.rgdi_store.services;

import java.util.List;

import com.devsoft.rgdi_store.entities.ProductImageEntity;

public interface ProductImageService {
    ProductImageEntity save(ProductImageEntity productImage);
    ProductImageEntity findById(Long id);
    List<ProductImageEntity> findAll();
    void deleteById(Long id);
}
