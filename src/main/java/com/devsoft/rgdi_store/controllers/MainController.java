package com.devsoft.rgdi_store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String login() {
        return "login"; // Redireciona para o arquivo login.html
    }
    @GetMapping("/home")
    public String home() {
    	return "redirect:/auth"; // Redireciona para /auth
    }
    
    @GetMapping("/auth")
    public String home1() {
        return "home"; // Redireciona para o arquivo home.html
    }

    @GetMapping("/h2-console")
    public String redirectToH2Console() {
        return "redirect:/h2-console"; // Redireciona para o console do H2
    }

}

