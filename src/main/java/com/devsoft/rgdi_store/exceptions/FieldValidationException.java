package com.devsoft.rgdi_store.services.exceptions;

import java.util.List;

import org.springframework.validation.FieldError;

public class FieldValidationException extends IllegalArgumentException {
    
	private static final long serialVersionUID = 1L;
	private final List<FieldError> fieldErrors;

    public FieldValidationException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}

