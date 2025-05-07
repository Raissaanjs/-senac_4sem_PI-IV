package com.devsoft.rgdi_store.exceptions.all;

public class EmailExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailExistsException(String msg) {
        super(msg);
    }
}