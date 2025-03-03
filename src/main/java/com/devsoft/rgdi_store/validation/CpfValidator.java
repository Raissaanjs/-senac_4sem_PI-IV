package com.devsoft.rgdi_store.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false; // CPF nulo ou vazio é inválido
        }

        // Remove caracteres não numéricos do CPF
        String cpf = value.replaceAll("\\D", "");

        // Valida o tamanho do CPF e aplica regras de validação
        if (cpf.length() != 11 || hasAllDigitsEqual(cpf)) {
            return false; // CPF precisa ter 11 dígitos e não pode ter todos os dígitos iguais
        }

        return isValidCpf(cpf);
    }

    private boolean hasAllDigitsEqual(String cpf) {
        // Verifica se todos os dígitos são iguais
        return cpf.chars().allMatch(ch -> ch == cpf.charAt(0));
    }

    private boolean isValidCpf(String cpf) {
        try {
            // Cálculo do primeiro dígito verificador
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int firstVerifier = 11 - (sum1 % 11);
            if (firstVerifier >= 10) {
                firstVerifier = 0;
            }

            // Cálculo do segundo dígito verificador
            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int secondVerifier = 11 - (sum2 % 11);
            if (secondVerifier >= 10) {
                secondVerifier = 0;
            }

            // Verifica se os dígitos verificadores correspondem
            return firstVerifier == Character.getNumericValue(cpf.charAt(9)) &&
                   secondVerifier == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            // Qualquer erro na validação retorna falso
            return false;
        }
    }
}
