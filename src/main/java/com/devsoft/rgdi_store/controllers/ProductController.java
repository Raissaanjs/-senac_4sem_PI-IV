package com.devsoft.rgdi_store.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.dto.ProductDto;
import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.services.ProductService;

@Controller
@RequestMapping("/produtos")
public class ProductController {

	private final UploadConfig uploadConfig;

    public ProductController(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }
	
    @Autowired
    private ProductService productService;

    // Método para listar todos os produtos
    @GetMapping("/listar")
    public String listProducts(Model model) {
        model.addAttribute("produtos", productService.findAll());
        return "produto/listproduct"; // Nome da página Thymeleaf
    }

    // Método para exibir o formulário de criação de produto
    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "produto/cadproduct"; // Nome da página Thymeleaf
    }

    // Método para salvar um novo produto
    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute ProductDto productDto, @RequestParam("imageFiles") MultipartFile[] files, RedirectAttributes redirectAttributes) {
        ProductEntity productEntity = new ProductEntity();
        // Copiar propriedades de productDto para productEntity
        productEntity.setNome(productDto.getNome());
        productEntity.setPreco(productDto.getPreco());
        productEntity.setQuantidade(productDto.getQuantidade());
        productEntity.setDescricao(productDto.getDescricao());
        productEntity.setAvaliacao(productDto.getAvaliacao());
        productEntity.setStatus(productDto.isStatus());

        // Adicionar imagens ao produto
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    // Salvar o arquivo na pasta de uploads
                    Path path = Paths.get(uploadConfig.getUploadDir(), file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    // Criar entidade de imagem e adicionar ao produto
                    ProductImageEntity imageEntity = new ProductImageEntity();
                    imageEntity.setUrl("/uploads/" + file.getOriginalFilename());
                    imageEntity.setProductEntity(productEntity);
                    productEntity.getImagens().add(imageEntity);

                } catch (IOException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("message", "Falha ao carregar '" + file.getOriginalFilename() + "'");
                    return "redirect:/produtos/cadastrar";
                }
            }
        }

        // Salvar produto no banco de dados
        productService.save(productEntity);

        redirectAttributes.addFlashAttribute("message", "Produto salvo com sucesso!");
        return "redirect:/produtos/listar";
    }

    // Método para exibir o formulário de edição de produto
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "produto/cadproduct"; // Nome da página Thymeleaf
        } else {
        	return "redirect:/produtos/listar";
        }
    }

    // Método para deletar um produto
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/produtos/listar";
    }
}
