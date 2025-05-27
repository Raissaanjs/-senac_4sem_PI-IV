package com.devsoft.rgdi_store.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.devsoft.rgdi_store.dto.ProdutoImagensDto;
import com.devsoft.rgdi_store.entities.ProdutoEntity;
import com.devsoft.rgdi_store.services.ProdutoImagensService;
import com.devsoft.rgdi_store.services.ProdutoService;

@Controller
public class MainController {
	
	private final ProdutoService produtoService;
	private final ProdutoImagensService produtoImagensService;	
	
	public MainController(ProdutoService produtoService,
						  ProdutoImagensService produtoImagensService) {
		this.produtoService = produtoService;
		this.produtoImagensService = produtoImagensService;		
	}
   
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

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("produtos", produtosLoja);
        model.addAttribute("imagensPrincipais", imagensPrincipais);

        return "index"; // View que retorna a página inicial
    }
	
	@GetMapping("/login")
    public String loginAdmin(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        // Se encontrar erro no login manda a mensagem abaixo para View
		if (error != null) {
            model.addAttribute("erro", "Verifique Cadastro: Email, Senha ou se está Ativo!");
        }
        return "login"; // View que retorna a página de login - ADMIN
    }
    
    @GetMapping("/front-adm")
    public String frontAdm() {
        return "frontadm"; // View que retorna a página de seleção (frontadm.html)
    }
    
    @GetMapping("/logout")
    public String logoutAdmin() {
        return "login"; // View que retorna a página de login - ADMIN
    }    
    
    @GetMapping("/sessao-expirada")
    public String sessaoExpirada() {
        return "sessao-expirada-cliente"; // View que retorna a página sessao-expirada-cliente
    }
    
    @GetMapping("/admin")
    public String posLogin() {
        return "home-admin"; // View que retorna a página inicial do Painel Administrativo
    }
    
    // Endpoint de autenticação
    @GetMapping("/username")
    public String getUsername() {
    	// Acessa o contexto de segurança do Spring
        Authentication authentication =
        		SecurityContextHolder.getContext() //Retorna o contexto de segurança atual
        		// Retorna o objeto Authentication, que contém: Nome do usuário, Credenciais, Permissões, Se sucesso na autenticação
        		.getAuthentication(); 
        return authentication.getName(); // Retorna o usuário autenticado (email)
    }
    
}

