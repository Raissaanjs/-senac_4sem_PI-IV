package com.devsoft.rgdi_store.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.services.exceptions.FieldValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class UserValidator {

    private final MessageSource messageSource;

    public UserValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void validatePass(UserDto dto) {
        List<FieldError> fieldErrors = new ArrayList<>();

        if (!dto.getSenha().equals(dto.getConfirmasenha())) {
            String errorMessage = messageSource.getMessage("senha.naoCoincidem", null, Locale.getDefault());
            fieldErrors.add(new FieldError("User", "senha/confirmasenha", errorMessage));
        }

        if (!fieldErrors.isEmpty()) {
            String errorTitle = messageSource.getMessage("validacao.erro", null, Locale.getDefault());
            throw new FieldValidationException(errorTitle, fieldErrors);
        }
    }
}
