package com.devsoft.rgdi_store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

//Implementação do validador personalizado
public class CustomEmailValidator implements ConstraintValidator<ValidEmail, String> {
    private static final String EMAIL_PATTERN =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("O email não pode ser nulo.").addConstraintViolation();
            return false;
        }
        if (!pattern.matcher(email).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Formato de email inválido.").addConstraintViolation();
            return false;
        }
        return true;
    }

}
