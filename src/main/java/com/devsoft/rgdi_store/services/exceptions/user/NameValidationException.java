package com.devsoft.rgdi_store.services.exceptions.user;

public class NameValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NameValidationException(String msg) {
        super(msg);
    }
}