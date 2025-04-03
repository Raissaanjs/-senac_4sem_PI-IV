package com.devsoft.rgdi_store.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.devsoft.rgdi_store.repositories.UserRepository;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    public AuthenticationService(UserRepository userRepository, PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    public boolean authenticate(String email, String senha) {
        if (email == null || senha == null) {
            logger.error("Tentativa de autenticação com email ou senha nulos");
            throw new IllegalArgumentException("Email e senha não podem ser nulos");
        }

        return userRepository.findByEmail(email)
                .map(userFromDb -> {
                    boolean isMatch = passwordUtils.matches(senha, userFromDb.getSenha());
                    logger.info("Autenticação para o email {} {}", email, (isMatch ? "bem-sucedida" : "falhou"));
                    return isMatch;
                })
                .orElseGet(() -> {
                    logger.warn("Usuário não encontrado para o email: {}", email);
                    return false;
                });
    }
}


