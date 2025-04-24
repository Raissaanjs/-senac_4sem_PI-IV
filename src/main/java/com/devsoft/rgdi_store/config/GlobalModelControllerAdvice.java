package com.devsoft.rgdi_store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final UserRepository userRepository;  // Injeção do seu repositório de usuários   
    private final CarrinhoService carrinhoService;
    private final ClienteRepository clienteRepository;
    

    public GlobalModelControllerAdvice(UserRepository userRepository,
    									CarrinhoService carrinhoService, 
    									ClienteRepository clienteRepository) {
        this.userRepository = userRepository;
        this.carrinhoService = carrinhoService;
        this.clienteRepository = clienteRepository;    
    }

    @ModelAttribute
    public void addAttributesToModel(Authentication authentication, Model model) {
        if (authentication != null) {
            String email = authentication.getName();

         // 🔍 Tenta buscar como admin
            UserEntity user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                model.addAttribute("userName", user.getNome());
            } else {
                // 🔍 Se não for admin, tenta buscar como cliente
                ClienteEntity cliente = clienteRepository.findByEmail(email).orElse(null);
                if (cliente != null) {
                    model.addAttribute("userName", cliente.getNome());
                } else {
                    model.addAttribute("userName", "Visitante");
                }
            }

            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            model.addAttribute("isEstoque", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTOQ")));
            model.addAttribute("isUser", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
            model.addAttribute("isClient", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT")));

           
            // Novo: pega o grupo como texto amigável
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
            model.addAttribute("userName", "Guest");
            model.addAttribute("userGroup", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("isEstoque", false);
            model.addAttribute("isUser", false);
            model.addAttribute("isClient", false);
        }


        // Adiciona o número total de itens no carrinho ao modelo
        int totalItens = carrinhoService.getQuantidadeTotalItens(); // Método que calcula a quantidade total de itens no carrinho
        model.addAttribute("totalItens", totalItens);  // Adiciona a quantidade de itens no carrinho ao modelo
    }
}
