package com.devsoft.rgdi_store.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.repositories.ProductImageRepository;

@Service
public class ProductImageService {
	
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private UploadConfig uploadConfig;

    @Transactional
    public ProductImageEntity save(ProductImageEntity productImage) {
        return productImageRepository.save(productImage);
    }

    @Transactional(readOnly = true)
    public ProductImageEntity findById(Long id) {
        return productImageRepository.findById(id).orElse(null);
    }    
    
    @Transactional(readOnly = true)
    public Page<ProductImageEntity> findAll(Pageable pageable) {
        if (pageable == null) {
            pageable = PageRequest.of(0, 5); // Página 0 com 5 itens por padrão
        }
        Page<ProductImageEntity> result = productImageRepository.findAll(pageable);
        return result;
    }   

    @Transactional
    public void deleteById(Long id) {
        productImageRepository.deleteById(id);
    }

    @Transactional
    public String saveImage(MultipartFile file) {
        // Verifica se o arquivo é uma imagem
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("O arquivo deve ser uma imagem");
        }

        String uploadDir = uploadConfig.getUploadDir();
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a imagem", e);
        }
    }

}
