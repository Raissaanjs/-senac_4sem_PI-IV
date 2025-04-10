package com.devsoft.rgdi_store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.CarrinhoService;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    @Autowired
    private UserRepository userRepository;  // Injeção do seu repositório de usuários
    
    @Autowired
    private CarrinhoService carrinhoService;

    public GlobalModelControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addAttributesToModel(Authentication authentication, Model model) {
        // Adiciona o nome do usuário ao modelo, se autenticado
        if (authentication != null) {
            String email = authentication.getName();  // Pega o nome do usuário (geralmente o e-mail)

            UserEntity user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("userName", user.getNome());  // Nome do usuário autenticado
            } else {
                model.addAttribute("userName", "Visitante");  // Caso não encontre, coloca como "Visitante"
            }

            // Verifica as funções do usuário
            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            model.addAttribute("isEstoque", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ESTOQ")));
            model.addAttribute("isUser", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        } else {
            model.addAttribute("userName", "Guest");  // Se não estiver autenticado, coloca "Guest"
            model.addAttribute("isAdmin", false);
            model.addAttribute("isEstoque", false);
            model.addAttribute("isUser", false);
        }

        // Adiciona o número total de itens no carrinho ao modelo
        int totalItens = carrinhoService.getQuantidadeTotalItens(); // Método que calcula a quantidade total de itens no carrinho
        model.addAttribute("totalItens", totalItens);  // Adiciona a quantidade de itens no carrinho ao modelo
    }
}
