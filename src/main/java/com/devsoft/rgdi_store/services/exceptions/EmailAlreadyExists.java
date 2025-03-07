package com.devsoft.rgdi_store.services.exceptions;

public class EmailAlreadyExists extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyExists(String msg) {
        super(msg);
    }
}