package com.devsoft.rgdi_store.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.devsoft.rgdi_store.entities.UserGroup;

@Controller
public class RoleRedirectController {

    @GetMapping("/auth-base")
    public String redirectBasedOnRole(Authentication authentication) {
        // Verifica se há um usuário autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            // Obtém o grupo (role) do usuário autenticado
            String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Extrai o nome da autoridade, como ROLE_ADMIN
                .findFirst()
                .orElse(null);

            // Converte a autoridade em um UserGroup, se disponível
            if (authority != null) {
                try {
                    UserGroup grupo = UserGroup.fromGrupo(authority);

                    switch (grupo) {
                        case ROLE_ADMIN:
                            return "redirect:/auth-redirect"; // Painel do administrador
                        case ROLE_ESTOQ:
                            return "redirect:/inventory-path"; // Estoque
                        case ROLE_USER:
                            return "redirect:/error-no-perm"; // Erro de permissão
                        default:
                            return "redirect:/login"; // Caso padrão
                    }
                } catch (IllegalArgumentException e) {
                    // Caso a autoridade não seja válida, redireciona para login
                    return "redirect:/login";
                }
            }
        }
        // Se o usuário não estiver autenticado, redireciona para login
        return "redirect:/login";
    }
}



