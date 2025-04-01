package com.devsoft.rgdi_store.validation.base;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false; // E-mail nulo ou vazio é inválido
        }
        return pattern.matcher(email).matches(); // Verifica o padrão
    }   
}

