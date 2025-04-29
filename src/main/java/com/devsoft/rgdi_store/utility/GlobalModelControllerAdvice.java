package com.devsoft.rgdi_store.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devsoft.rgdi_store.authentication.CustomClienteDetails;
import com.devsoft.rgdi_store.authentication.CustomUserDetails;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final CarrinhoService carrinhoService;

    public GlobalModelControllerAdvice(UserRepository userRepository, ClienteRepository clienteRepository, CarrinhoService carrinhoService) {
        this.userRepository = userRepository;
        this.clienteRepository = clienteRepository;
        this.carrinhoService = carrinhoService;
    }

    @ModelAttribute
    public void addAttributesToModel(Authentication authentication, Model model) {
        String nomeExibicao = "Visitante";

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            // Se for admin (usuário)
            if (principal instanceof CustomUserDetails userDetails) {
                nomeExibicao = userRepository.findByEmail(userDetails.getUsername())
                        .map(UserEntity::getNome)
                        .orElse("Usuário");

            // Se for cliente
            } else if (principal instanceof CustomClienteDetails clienteDetails) {
                nomeExibicao = clienteRepository.findByEmail(clienteDetails.getUsername())
                        .map(ClienteEntity::getNome)
                        .orElse("Cliente");
            }

            // Nome do usuário/cliente
            model.addAttribute("userName", nomeExibicao);

            // Roles
            model.addAttribute("isAdmin", hasAuthority(authentication, "ROLE_ADMIN"));
            model.addAttribute("isEstoque", hasAuthority(authentication, "ROLE_ESTOQ"));
            model.addAttribute("isUser", hasAuthority(authentication, "ROLE_USER"));
            model.addAttribute("isClient", hasAuthority(authentication, "ROLE_CLIENT"));

            // Grupo (admin) - descrição amigável
            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            try {
                UserGroup userGroup = UserGroup.fromGrupo(role);
                model.addAttribute("userGroup", userGroup.getDescricao());
            } catch (IllegalArgumentException e) {
                model.addAttribute("userGroup", "Desconhecido");
            }

        } else {
            // Usuário não autenticado
            model.addAttribute("userName", "Guest");
            model.addAttribute("userGroup", "Visitante");
            model.addAttribute("isAdmin", false);
            model.addAttribute("isEstoque", false);
            model.addAttribute("isUser", false);
            model.addAttribute("isClient", false);
        }

        // Total de itens no carrinho
        int totalItens = carrinhoService.getQuantidadeTotalItens();
        model.addAttribute("totalItens", totalItens);
    }

    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
}

