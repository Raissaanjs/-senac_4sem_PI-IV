package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Método para listar todos os produtos
    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    // Método para encontrar um produto por ID
    public Optional<ProductEntity> findById(Long id) {
        return productRepository.findById(id);
    }

    // Método para salvar um novo produto
    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }

    // Método para atualizar um produto existente
    public ProductEntity update(ProductEntity product) {
        return productRepository.save(product);
    }

    // Método para deletar um produto por ID
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}

