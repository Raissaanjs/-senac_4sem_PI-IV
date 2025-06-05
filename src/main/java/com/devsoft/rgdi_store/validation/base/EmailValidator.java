package com.devsoft.rgdi_store.validation.base;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // defne os parâmetros aceito no e-email
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN); // usado para validar o e-mail baseado String acima
    
    // NULL - não existe/ definido/ carregado/ inicializado		VAZIO - Existe, mas está vazio
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false; // E-mail nulo ou vazio é inválido
        }
        // pattern.matcher(email): cria um objeto para testar o email
        // .matches(): verifica se o email combina totalmente com o padrão
        return pattern.matcher(email).matches(); // Verifica se atende o padrão (Retorno true/ false)
    }   
}

