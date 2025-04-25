package com.devsoft.rgdi_store.services.exceptions;

public class EnderecoDuplicadoException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public EnderecoDuplicadoException(String message) {
        super(message);
    }
}

