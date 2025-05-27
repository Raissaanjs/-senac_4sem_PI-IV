package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes/error")
public class ErrorClientController {
    
	// Erro de login de cliente
	@GetMapping("/error-login-cliente")  // Spring está gerenciando
    public String loginErrorPageLoja() {
        return "error/error-cliente-login"; // Renderiza o template error-login.html
    }
	
	// Cliente não autenticado tentando usar um endpoint que exige autenticação
    @GetMapping("/error-no-auth-cliente")  // Spring está gerenciando
    public String loginErrorAutCliente() {
        return "error/error-cliente-no-authent"; // Renderiza o template error-client-no-authent.html
    }
		
	// Acesso negado - Não pode acessar
	@GetMapping("/error-no-perm-cliente")  // Spring está gerenciando
    public String errorPermCliente() {
        return "error/error-cliente-access-denied"; // Renderiza o template access-denied.html
    }	
	
}

