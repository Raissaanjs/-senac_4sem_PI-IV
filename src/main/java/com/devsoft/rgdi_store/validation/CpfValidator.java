package com.devsoft.rgdi_store.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CpfValidator implements ConstraintValidator<ValidCPF, String> {
    private static final String CPF_PATTERN = "^[0-9]{11}$";

    private Pattern pattern = Pattern.compile(CPF_PATTERN);

    @Override
    public void initialize(ValidCPF constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || !pattern.matcher(cpf).matches()) {
            return false;
        }
        return isCpfValid(cpf);
    }

    private boolean isCpfValid(String cpf) {
        int[] cpfArray = new int[11];
        for (int i = 0; i < 11; i++) {
            cpfArray[i] = Character.getNumericValue(cpf.charAt(i));
        }

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += cpfArray[i] * (10 - i);
        }
        int check1 = 11 - (sum1 % 11);
        if (check1 >= 10) {
            check1 = 0;
        }

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += cpfArray[i] * (11 - i);
        }
        int check2 = 11 - (sum2 % 11);
        if (check2 >= 10) {
            check2 = 0;
        }

        return cpfArray[9] == check1 && cpfArray[10] == check2;
    }
}

