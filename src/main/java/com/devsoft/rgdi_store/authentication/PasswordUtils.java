package com.devsoft.rgdi_store.authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtils {

    private final BCryptPasswordEncoder encoder;

    // Construtor com fator de força customizado (12 por padrão, pode ajustar se necessário)
    public PasswordUtils() {
        this.encoder = new BCryptPasswordEncoder(12);
    }

    // Método para criptografar a senha
    public String encrypt(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia - criptografando a senha[class PasswordUtils] ");
        }
        return encoder.encode(password);
    }

    // Método para verificar se a senha fornecida corresponde à senha armazenada
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            throw new IllegalArgumentException("As senhas não podem ser nulas - Ver se corresponde à senha armazenada [class PasswordUtils]");
        }
        return encoder.matches(rawPassword, encodedPassword);
    }
}


