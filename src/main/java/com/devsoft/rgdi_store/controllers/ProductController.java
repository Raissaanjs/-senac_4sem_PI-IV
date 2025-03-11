package com.devsoft.rgdi_store.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devsoft.rgdi_store.entities.ProductEntity;
import com.devsoft.rgdi_store.services.ProductService;

@Controller
@RequestMapping("/produtos")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Método para listar todos os produtos
    @GetMapping("/listar")
    public String listProducts(Model model) {
        model.addAttribute("produtos", productService.findAll());
        return "produto/product-list"; // Nome da página Thymeleaf
    }

    // Método para exibir o formulário de criação de produto
    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new ProductEntity());
        return "produto/product-form"; // Nome da página Thymeleaf
    }

    // Método para salvar um novo produto
    @PostMapping
    public String saveProduct(@ModelAttribute("product") ProductEntity product) {
        productService.save(product);
        return "redirect:/produtos/listar";
    }

    // Método para exibir o formulário de edição de produto
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<ProductEntity> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "produto/product-form"; // Nome da página Thymeleaf
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
