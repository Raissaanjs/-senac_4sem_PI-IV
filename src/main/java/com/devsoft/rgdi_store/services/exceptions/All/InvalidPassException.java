package com.devsoft.rgdi_store.services.exceptions.All;

public class InvalidPassException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public InvalidPassException(String message) {
        super(message);
    }
}

