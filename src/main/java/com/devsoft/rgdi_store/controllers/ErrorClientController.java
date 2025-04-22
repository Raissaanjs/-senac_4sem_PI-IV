package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clientes/error")
public class ErrorClientController {
    
	@GetMapping("/error-no-perm-cliente")
    public String errorPermCliente() {
        return "error/error-cliente-access-denied"; // Renderiza o template access-denied.html
    }
	
	@GetMapping("/error-cliente-inat")
    public String loginErrorClienteInative() {
        return "error/error-cliente-inative"; // Renderiza o template error-user-inative.html
    }
	
	@GetMapping("/error-login-cliente")
    public String loginErrorPageLoja() {
        return "error/error-cliente-login"; // Renderiza o template error-login.html
    }      
    
    @GetMapping("/error-no-auth-cliente")
    public String loginErrorAutCliente() {
        return "error/error-cliente-no-authent"; // Renderiza o template error-client-no-authent.html
    }
}

