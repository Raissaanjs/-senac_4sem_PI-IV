package com.devsoft.rgdi_store.validation.user;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.exceptions.all.ConfirmPassNullException;
import com.devsoft.rgdi_store.exceptions.all.EmailDivergException;
import com.devsoft.rgdi_store.exceptions.all.EmailExistsException;
import com.devsoft.rgdi_store.exceptions.all.InvalidCpfException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.exceptions.all.NameValidationException;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.validation.base.ConfirmPassValidationNull;
import com.devsoft.rgdi_store.validation.base.CpfValidator;
import com.devsoft.rgdi_store.validation.base.EmailValidator;
import com.devsoft.rgdi_store.validation.base.NameValidation;
import com.devsoft.rgdi_store.validation.base.PassValidation;

public class UserValidationSaveService {

    // Método principal para validar todos os campos do usuário
    public static void validateUser(UserDto dto, UserRepository repository) {
        validateName(dto.getNome());
        validateCpf(dto.getCpf());
        validateEmail(dto.getEmail(), repository);
        validatePassword(dto.getSenha(), dto.getConfirmasenha());
    }

    // Validação do nome
    private static void validateName(String nome) {
        if (!NameValidation.isValidName(nome)) {
            throw new NameValidationException("Nome inválido. Deve conter entre 3 e 200 caracteres.");
        }
    }

    // Validação de CPF
    private static void validateCpf(String cpf) {
        if (!CpfValidator.isValidCPF(cpf)) {
            throw new InvalidCpfException("O CPF informado é inválido.");
        }
    }

    // Validação de e-mail e checagem de duplicidade
    private static void validateEmail(String email, UserRepository repository) {
        if (!EmailValidator.isValidEmail(email)) {
            throw new EmailDivergException("O e-mail informado está vazio ou em formato inválido ([email@email.com]).");
        }
        if (repository.existsByEmail(email)) {
            throw new EmailExistsException("O e-mail informado já está cadastrado no sistema.");
        }
    }

    // Validação de senha e confirmação de senha
    private static void validatePassword(String senha, String confirmasenha) {
        if (!PassValidation.isValidPassword(senha)) {
            throw new InvalidPassException("A senha não pode ser nula e deve ter no mínimo 6 caracteres.");
        }
        if (!ConfirmPassValidationNull.isPasswordMatching(senha, confirmasenha)) {
            throw new ConfirmPassNullException("As senhas não coincidem.");
        }
    }
}
