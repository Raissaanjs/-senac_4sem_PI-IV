package com.devsoft.rgdi_store.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

//Implementação do validador personalizado
	public class CustomEmailValidator implements ConstraintValidator<ValidEmail, String> {
	    private static final String EMAIL_PATTERN =
	        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

	    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	    @Override
	    public void initialize(ValidEmail constraintAnnotation) {
	    }

	    @Override
	    public boolean isValid(String email, ConstraintValidatorContext context) {
	        return email != null && pattern.matcher(email).matches();
	    }
}
