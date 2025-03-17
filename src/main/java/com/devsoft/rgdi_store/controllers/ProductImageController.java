package com.devsoft.rgdi_store.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.services.ProductImageService;

@Controller
@RequestMapping("/product-images")
public class ProductImageController {	
	
    @Autowired
    private ProductImageService productImageService;
    
    private final Path uploadConfig;

    public ProductImageController(UploadConfig uploadConfig) {
        this.uploadConfig = Paths.get(uploadConfig.getUploadDir())
                .toAbsolutePath().normalize();
    }

    @GetMapping("/listar")
    public String list(Model model,
            @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable) {
        Page<ProductImageEntity> dtoPage = productImageService.findAll(pageable);

        // Adiciona os resultados da página ao modelo do Thymeleaf
        model.addAttribute("produtosImage", dtoPage.getContent());
        model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

        return "produto/produto-imagens/listimage"; // Template Thymeleaf
    }

    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("productImage", new ProductImageEntity());
        return "/produto/product-images/cadimage";
    }

    @PostMapping("/create")
    public String createProductImage(@ModelAttribute("productImage") ProductImageEntity productImage) {
        productImageService.save(productImage);
        return "redirect:/product-images/listar";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("imageFile") MultipartFile file) {
        
    	try {
            // Obtém o diretório de upload
            String uploadDir = ((UploadConfig) uploadConfig).getUploadDir();           

            // Salva o arquivo no diretório de upload
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Retorna a URL da imagem salva
            return "/uploads/" + fileName;  // A URL de acesso ao arquivo
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao salvar a imagem: " + e.getMessage();
        }
    	
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProductImageEntity productImage = productImageService.findById(id);
        if (productImage != null) {
            model.addAttribute("productImage", productImage);
            return "/produto/produto-imagens/editimage";
        } else {
            return "redirect:/product-images";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProductImage(@PathVariable Long id, @ModelAttribute("productImage") ProductImageEntity productImage) {
        productImage.setId(id);
        productImageService.save(productImage);
        return "redirect:/product-images";
    }

    @GetMapping("/delete/{id}")
    public String deleteProductImage(@PathVariable Long id) {
        productImageService.deleteById(id);
        return "redirect:/product-images";
    }
}