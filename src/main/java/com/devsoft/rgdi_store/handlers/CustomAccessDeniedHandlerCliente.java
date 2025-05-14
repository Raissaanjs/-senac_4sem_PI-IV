package com.devsoft.rgdi_store.handlers;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class CustomAccessDeniedHandlerCliente implements AccessDeniedHandler {

	@Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Verificar se o usuário está autenticado
        if (request.getUserPrincipal() == null) {
            // Se o usuário não estiver autenticado, redireciona para o login
            response.sendRedirect("/clientes/login");
        } else {
            // Caso contrário, redireciona para a página de erro personalizada de permissão negada
            response.sendRedirect("/clientes/error/error-no-perm-cliente");
        }
    }
}

