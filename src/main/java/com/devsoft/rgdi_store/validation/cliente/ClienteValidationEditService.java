package com.devsoft.rgdi_store.validation.cliente;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.exceptions.All.ConfirmPassNullException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidPassException;
import com.devsoft.rgdi_store.services.exceptions.All.NameValidationException;
import com.devsoft.rgdi_store.validation.base.ConfirmPassValidationNull;
import com.devsoft.rgdi_store.validation.base.NameValidation;
import com.devsoft.rgdi_store.validation.base.PassValidation;

public class ClienteValidationEditService {

    public static void validateAndUpdateCliente(ClienteEntity novo, ClienteEntity existente, ClienteRepository repository) {
        if (novo == null || existente == null) return;

        validateAndUpdateName(novo.getNome(), existente);
        validateAndUpdatePassword(novo.getSenha(), novo.getSenha(), existente); // novamente, sem confirmação separada
        // Outros campos podem ser validados aqui também, se necessário
    }

    // Valida e atualiza o nome
    public static void validateAndUpdateName(String nome, ClienteEntity entity) {
        if (nome != null && !nome.isEmpty()) {
            if (!NameValidation.hasValidWords(nome)) {
            	throw new NameValidationException("Nome inválido. Deve conter pelo menos duas palavras com no mínimo"
            			+ " 3 letras cada, exceto 'de' e 'da'.");
            }
            entity.setNome(nome);
        }
    }

    // Valida e atualiza a senha
    public static void validateAndUpdatePassword(String senha, String confirmasenha, ClienteEntity entity) {
        if (senha != null && !senha.isEmpty()) {
            if (!PassValidation.isValidPassword(senha)) {
                throw new InvalidPassException("A senha não pode ser nula e deve ter no mínimo 6 caracteres");
            }
            if (!ConfirmPassValidationNull.isPasswordMatching(senha, confirmasenha)) {
                throw new ConfirmPassNullException("As senhas não coincidem");
            }
            entity.setSenha(senha); // Idealmente, criptografar depois
        }
    }
}

