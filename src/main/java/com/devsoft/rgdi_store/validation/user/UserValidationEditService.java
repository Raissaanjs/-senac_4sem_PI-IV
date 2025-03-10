package com.devsoft.rgdi_store.validation.user;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.entities.UserEntity;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.exceptions.user.ConfirmPassNullException;
import com.devsoft.rgdi_store.services.exceptions.user.InvalidCpfException;
import com.devsoft.rgdi_store.services.exceptions.user.InvalidPassException;
import com.devsoft.rgdi_store.services.exceptions.user.NameValidationException;

public class UserValidationEditService {

    // Método principal para validar e atualizar os campos do usuário
	public static void validateAndUpdateUser(UserDto dto, UserEntity entity, UserRepository repository) {
	    if (dto == null || entity == null) {
	        return; // Retorna caso DTO ou Entidade sejam nulos
	    }
	}


    // Valida e atualiza o nome
    public static void validateAndUpdateName(String nome, UserEntity entity) {
        if (nome != null && !nome.isEmpty()) {
            if (!NameValidation.isValidName(nome)) {
                throw new NameValidationException("Nome inválido. Deve conter entre 3 e 150 caracteres");
            }
            entity.setNome(nome); // Atualiza o nome na entidade
        }
    }

    // Valida e atualiza o CPF
    public static void validateAndUpdateCpf(String cpf, UserEntity entity) {
        if (cpf != null && !cpf.isEmpty()) {
            if (!CpfValidator.isValidCPF(cpf)) {
                throw new InvalidCpfException("O CPF informado é inválido");
            }
            entity.setCpf(cpf); // Atualiza o CPF na entidade
        }
    }

    // Valida e atualiza a senha
    public static void validateAndUpdatePassword(String senha, String confirmasenha, UserEntity entity) {
        if (senha != null && !senha.isEmpty()) {
            if (!PassValidation.isValidPassword(senha)) {
                throw new InvalidPassException("A senha não pode ser nula e deve ter no mínimo 6 caracteres");
            }
            if (!ConfirmPassValidationNull.isPasswordMatching(senha, confirmasenha)) {
                throw new ConfirmPassNullException("As senhas não coincidem");
            }
            entity.setSenha(senha); // Atualiza a senha na entidade
        }
    }
    
}
