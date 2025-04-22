package com.devsoft.rgdi_store.controllers.handlers;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomNoAuthenticatedHandlerLoja implements AuthenticationEntryPoint {
	
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                         AuthenticationException authException) throws IOException {
        response.sendRedirect("/error-no-auth-cliente"); // Redireciona para endpoint de erro
    }
}
