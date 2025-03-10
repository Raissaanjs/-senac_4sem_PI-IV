package com.devsoft.rgdi_store.services.exceptions.user;

public class EmailDivergException extends RuntimeException {
   
	private static final long serialVersionUID = 1L;

	public EmailDivergException(String message) {
        super(message);
    }
}