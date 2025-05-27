package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorUserController {

	// Erro de login
    @GetMapping("/error-login") // Spring está gerenciando
    public String loginErrorPage() {
        return "error/error-user-login"; // Renderiza o template error-login.html
    }
           
    // Usuário não autenticado tentando usar um endpoint que exige autenticação
    @GetMapping("/error-no-auth") // Spring está gerenciando
    public String loginErrorAut() {
        return "error/error-user-no-authent"; // Renderiza o template error-user-no-authent.html
    }
    
    // Acesso negado - Não pode acessar
    @GetMapping("/error-no-perm")
    public String errorPerm() {
        return "error/error-user-access-denied"; // Renderiza o template access-denied.html
    }        
    
    // Cliente inativo
    @GetMapping("/error-user-inat") // Spring está gerenciando
    public String loginErrorInative() {
        return "error/error-user-inative"; // Renderiza o template error-user-inative.html
    }
   
    
}

