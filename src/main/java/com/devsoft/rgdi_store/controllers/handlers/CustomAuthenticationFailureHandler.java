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
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
		
		if (exception instanceof DisabledException) {
            response.sendRedirect("/error-user-inat"); // Redireciona para página personalizada de inativo
        } else if (exception instanceof BadCredentialsException) {
        	response.sendRedirect("/error-login"); // Página padrão de erro de login
        } else {
            response.sendRedirect("/error"); // Redireciona para uma página genérica de erro
        }
	}
}

