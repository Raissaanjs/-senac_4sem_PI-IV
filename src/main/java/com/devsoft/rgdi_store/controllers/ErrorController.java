package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error-login")
    public String loginErrorPage() {
        return "error/error-login"; // Renderiza o template error-login.html
    }
           
    @GetMapping("/error-no-auth")
    public String loginErrorAut() {
        return "error/error-user-no-authent"; // Renderiza o template error-user-no-authent.html
    }
    
    @GetMapping("/error-no-perm")
    public String errorPerm() {
        return "error/error-access-denied"; // Renderiza o template access-denied.html
    }
    
    @GetMapping("/error-user-inat")
    public String loginErrorInative() {
        return "error/error-user-inative"; // Renderiza o template error-user-inative.html
    }    
    
}

