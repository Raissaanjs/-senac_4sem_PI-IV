package com.devsoft.rgdi_store.services.exceptions.user;

public class InvalidCpfException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public InvalidCpfException(String message) {
        super(message);
    }
}