package com.devsoft.rgdi_store.validation.base;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError{
	
	private List<FieldMessage> errors = new ArrayList<>();
	
	public ValidationError(Integer status, String error) {
		super(status, error);
	}

	public List<FieldMessage> getErrors() {
		return errors;
	}

	//método para inserir o campo e a mensagem
	public void addError(String fieldName, String message) {
		errors.add(new FieldMessage(fieldName, message));
	}

}
