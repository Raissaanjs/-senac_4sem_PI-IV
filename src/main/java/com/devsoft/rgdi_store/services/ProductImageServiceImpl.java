package com.devsoft.rgdi_store.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.repositories.ProductImageRepository;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public ProductImageEntity save(ProductImageEntity productImage) {
        return productImageRepository.save(productImage);
    }

    @Override
    public ProductImageEntity findById(Long id) {
        return productImageRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductImageEntity> findAll() {
        return productImageRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }
}
