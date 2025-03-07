package com.devsoft.rgdi_store.services.exceptions;

public class PasswordConfirmationException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public PasswordConfirmationException(String message) {
        super(message);
    }
}

