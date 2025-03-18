package com.devsoft.rgdi_store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;
import com.devsoft.rgdi_store.entities.ProdutoImagens;
import com.devsoft.rgdi_store.services.ProdutoImagensService;

@Controller
@RequestMapping("/product-images")
public class ProdutoImagensController {	
	
    @Autowired
    private ProdutoImagensService produtoImagensService;
    
    private final UploadConfig uploadConfig;

    public ProdutoImagensController(UploadConfig uploadConfig) {
    	 this.uploadConfig = uploadConfig;
    }

    @GetMapping("/listar")
    public String list(Model model,
            @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable) {
        Page<ProdutoImagens> dtoPage = produtoImagensService.findAll(pageable);

        // Adiciona os resultados da página ao modelo do Thymeleaf
        model.addAttribute("produtosImage", dtoPage.getContent());
        model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

        return "produto/produto-imagens/listimage"; // Template Thymeleaf
    }

    @GetMapping("/cadastrar")
    public String showCreateForm(Model model) {
        model.addAttribute("productImage", new ProdutoImagens());
        return "/produto/product-images/cadimage";
    }

    @PostMapping("/create")
    public String inserir(@ModelAttribute("productImage") @RequestParam("id") Long id, @RequestParam("file") MultipartFile file) {
    	produtoImagensService.inserir(id, file);
        return "redirect:/product-images/listar";
    }   
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProdutoImagens produtoImagens = produtoImagensService.findById(id);
        if (produtoImagens != null) {
            model.addAttribute("productImage", produtoImagens);
            return "/produto/produto-imagens/editimage";
        } else {
            return "redirect:/product-images";
        }
    }

    /*
    @PostMapping("/update/{id}")
    public String updateProductImage(@PathVariable Long id, @ModelAttribute("productImage") MultipartFile file) {
        productImage.setId(id);
        produtoImagensService.inserir(productImage);
        return "redirect:/product-images";
    }
    */

    @GetMapping("/delete/{id}")
    public String deleteProductImage(@PathVariable Long id) {
    	produtoImagensService.deleteById(id);
        return "redirect:/product-images";
    }
}