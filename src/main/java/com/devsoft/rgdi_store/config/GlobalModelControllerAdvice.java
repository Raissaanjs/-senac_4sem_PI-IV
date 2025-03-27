package com.devsoft.rgdi_store.config;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalModelControllerAdvice {

    private final UserRepository userRepository;  // Injeção do seu repositório de usuários

    public GlobalModelControllerAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addUserNameToModel(Authentication authentication, Model model) {
        if (authentication != null) {
            // Pega o e-mail do usuário autenticado
            String email = authentication.getName();  // O nome do usuário geralmente é o e-mail

            // Busca o usuário pelo e-mail no banco de dados
            UserEntity user = userRepository.findByEmail(email).orElse(null);

            // Se o usuário for encontrado, adiciona o nome ao modelo
            if (user != null) {
                model.addAttribute("userName", user.getNome());  // Adiciona o nome do usuário ao modelo
            } else {
                model.addAttribute("userName", "Guest");  // Caso não encontre, coloca como "Guest"
            }
        } else {
            model.addAttribute("userName", "Guest");  // Se não estiver autenticado, coloca "Guest"
        }
    }
}
