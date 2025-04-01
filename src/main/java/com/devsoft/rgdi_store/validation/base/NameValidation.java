package com.devsoft.rgdi_store.validation.base;

import com.devsoft.rgdi_store.services.exceptions.All.NameValidationException;

public class NameValidation {

    // Valida nome: Mínimo 3 caracteres, Máximo 200
    public static boolean isValidName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        int length = name.length();
        return length >= 3 && length <= 200;
    }

    // Nome tem que ter 2 palavras e no mínimo 3 letras em cada palavra. Exceto: de, da
    public static boolean hasValidWords(String name) {
        String[] words = name.trim().split("\\s+");

        // Verifica se há pelo menos 2 palavras
        if (words.length < 2) {
            return false;
        }

        // Verifica o comprimento de cada palavra, ignorando "de" e "da"
        for (String word : words) {
            if (word.length() < 3 && !(word.equalsIgnoreCase("de") || word.equalsIgnoreCase("da"))) {
                return false;
            }
        }

        return true;
    }

    // Método para validar o nome
    public static void validateName(String nome) {
        if (!isValidName(nome)) {
            throw new NameValidationException("Nome inválido. Deve conter entre 3 e 200 caracteres.");
        }
        if (!hasValidWords(nome)) {
            throw new NameValidationException("Nome inválido. Deve conter pelo menos duas palavras com no mínimo 3 letras cada, exceto 'de' e 'da'.");
        }
    }
}