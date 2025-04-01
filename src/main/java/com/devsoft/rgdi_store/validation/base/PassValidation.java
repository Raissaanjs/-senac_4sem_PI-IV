package com.devsoft.rgdi_store.validation.user;

public class PassValidation {

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false; // Senha nula ou com menos de 6 caracteres é inválida
        }
        return true; // Senha válida
    }
}
