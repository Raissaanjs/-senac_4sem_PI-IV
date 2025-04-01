package com.devsoft.rgdi_store.validation.base;

public class ConfirmPassValidationNull {
	    
		public static boolean isPasswordMatching(String password, String confirmPassword) {
	        if (password == null || confirmPassword == null) {
	            return false; // Senha ou confirmação nulas são inválidas
	        }

	        return password.equals(confirmPassword); // Retorna true se as senhas forem iguais
	    }
	}
