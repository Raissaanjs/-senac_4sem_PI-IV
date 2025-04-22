package com.devsoft.rgdi_store.controllers.handlers;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandlerCliente implements AuthenticationFailureHandler {
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
		
		if (exception instanceof DisabledException) {
            response.sendRedirect("/clientes/error/error-cliente-inat"); // Redireciona para um endpoint cliente inativo
        } else if (exception instanceof BadCredentialsException) {
        	response.sendRedirect("/clientes/error/error-login-cliente"); // Endpoint padrão de erro de login
        } else {
            response.sendRedirect("/clientes/error/error-cliente-geral"); // Redireciona para um endpoint genérica de erro
        }
	}
}

