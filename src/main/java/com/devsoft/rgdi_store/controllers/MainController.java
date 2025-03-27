package com.devsoft.rgdi_store.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
public class MainController {
	
	@Autowired
	private ProdutoService produtoService;
	 
	@Autowired
	private ProdutoImagensService produtoImagensService;
   
	@GetMapping("/")
    public String listarProdutos(Model model) {
        // Lista de todos os produtos
        List<ProdutoEntity> produtosLoja = produtoService.findAllIndex();

        // Criar um mapa de imagens principais por produto
        Map<Long, ProdutoImagensDto> imagensPrincipais = new HashMap<>();
        for (ProdutoEntity produto : produtosLoja) {
            List<ProdutoImagensDto> imagens = produtoImagensService.buscarImagemPrincipalPorProdutoId(produto.getId());
            if (!imagens.isEmpty()) {
                imagensPrincipais.put(produto.getId(), imagens.get(0)); // Armazena a primeira imagem principal do produto
            }
        }

        // Passando os produtos e as imagens principais para a view
        model.addAttribute("produtos", produtosLoja);
        model.addAttribute("imagensPrincipais", imagensPrincipais);

        return "index"; // Nome do template Thymeleaf
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
    
    @GetMapping("/username")
    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Retorna o nome do usuário
    }
}

