package com.devsoft.rgdi_store.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
public class MainController {
	
	 @Autowired
	    private ProdutoService produtoService;
   
	
    @GetMapping("/")
    public String listarProdutos(Model model) {
        List<ProdutoEntity> produtosLoja = produtoService.findAllIndex();
        model.addAttribute("produtos", produtosLoja);
        return "index";  // Nome do template Thymeleaf
    }
    
    @GetMapping("/front-adm")
    public String frontAdm() {
        return "frontadm"; // Exibe a página seleção (frontadm.html)
    }
    
    @GetMapping("/admin")
    public String posLogin() {
        return "home-admin"; // Renderiza o template home-admin.html
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Exibe a página de login (login.html)
    }
    
    /*
    @GetMapping("/carrinho")
    public String carinho() {
        return "carrinho";
    }
    */
    
    /*
    @GetMapping("/upload-image")
    public String formUploadTest() { //APROVADO
        return "upload-file";
    }   
    */

    @GetMapping("/h2-console")
    public String redirectToH2Console() {
        return "redirect:/h2-console"; // Redireciona para o console do H2
    }
}

