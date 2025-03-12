package com.devsoft.rgdi_store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devsoft.rgdi_store.entities.ProductImageEntity;
import com.devsoft.rgdi_store.services.ProductImageService;

@Controller
@RequestMapping("/product-images")
public class ProductImageController {

    @Autowired
    private ProductImageService productImageService;

    @GetMapping("/listar")
    public String getAllProductImages(Model model) {
        List<ProductImageEntity> productImages = productImageService.findAll();
        model.addAttribute("productImages", productImages);
        return "/produto/produto-imagens/listimage";
    }

    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("productImage", new ProductImageEntity());
        return "/produto/product-images/cadimage";
    }

    @PostMapping
    public String createProductImage(@ModelAttribute("productImage") ProductImageEntity productImage) {
        productImageService.save(productImage);
        return "redirect:/product-images";
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