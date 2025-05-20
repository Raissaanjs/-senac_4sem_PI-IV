package com.devsoft.rgdi_store.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SessionExpiredFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isSessionInvalid = request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

        if (isSessionInvalid && isLoggedIn) {
            // Decide para onde redirecionar com base na URL
            if (path.startsWith("/clientes")) {
                response.sendRedirect("/clientes/sessao-expirada"); // testar funcionalidade
            } else {
                response.sendRedirect("/sessao-expirada");
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}
