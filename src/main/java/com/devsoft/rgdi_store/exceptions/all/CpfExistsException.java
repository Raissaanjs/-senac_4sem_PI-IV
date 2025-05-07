package com.devsoft.rgdi_store.exceptions.all;

public class CpfExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CpfExistsException(String msg) {
        super(msg);
    }
}