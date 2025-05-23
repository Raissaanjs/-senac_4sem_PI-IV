package com.devsoft.rgdi_store.validation.cliente;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.exceptions.all.ConfirmPassNullException;
import com.devsoft.rgdi_store.exceptions.all.CpfExistsException;
import com.devsoft.rgdi_store.exceptions.all.EmailDivergException;
import com.devsoft.rgdi_store.exceptions.all.EmailExistsException;
import com.devsoft.rgdi_store.exceptions.all.InvalidCpfException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.exceptions.all.NameValidationException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.validation.base.ConfirmPassValidationNull;
import com.devsoft.rgdi_store.validation.base.CpfValidator;
import com.devsoft.rgdi_store.validation.base.EmailValidator;
import com.devsoft.rgdi_store.validation.base.NameValidation;
import com.devsoft.rgdi_store.validation.base.PassValidation;

public class ClienteValidationSaveService {

    // Método principal para validar todos os campos do cliente
    public static void validateCliente(ClienteEntity cliente, ClienteRepository repository, String confirmaSenha) {
        validateName(cliente.getNome());
        validateCpf(cliente.getCpf(), repository);
        validateEmail(cliente.getEmail(), repository);
        validatePassword(cliente.getSenha(), confirmaSenha);
    }

    // Validação do nome
    public static void validateName(String nome) {
        if (!NameValidation.hasValidWords(nome)) {
            throw new NameValidationException("Nome inválido. Deve conter pelo menos duas palavras com no mínimo "
            		+ "3 letras cada, exceto 'de' e 'da'.");
        }
    }

    // Validação de CPF
    private static void validateCpf(String cpf, ClienteRepository repository) {
        if (!CpfValidator.isValidCPF(cpf)) {
            throw new InvalidCpfException("O CPF informado é inválido.");
        }
        if(repository.existsByCpf(cpf)) {
        	throw new CpfExistsException("O CPF informado já está cadastrado no sistema.");
        }
    }

    // Validação de e-mail e checagem de duplicidade
    private static void validateEmail(String email, ClienteRepository repository) {
        if (!EmailValidator.isValidEmail(email)) {
            throw new EmailDivergException("O e-mail informado está vazio ou em formato inválido ([email@email.com]).");
        }
        if (repository.existsByEmail(email)) {
            throw new EmailExistsException("O e-mail informado já está cadastrado no sistema.");
        }
    }

    // Validação de senha e confirmação de senha (temporário - sem campo separado de confirmação)
    public static void validatePassword(String senha, String confirmaSenha) {
        if (!PassValidation.isValidPassword(senha)) {
            throw new InvalidPassException("A senha não pode ser nula e deve ter no mínimo 6 caracteres.");
        }
        if (!ConfirmPassValidationNull.isPasswordMatching(senha, confirmaSenha)) {
            throw new ConfirmPassNullException("As senhas não coincidem.");
        }
    }
}
