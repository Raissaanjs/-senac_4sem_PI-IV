package com.devsoft.rgdi_store.exceptions;

public class DataIntegrityViolationException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public DataIntegrityViolationException(String msg) {
		super(msg);
	}
	

}
