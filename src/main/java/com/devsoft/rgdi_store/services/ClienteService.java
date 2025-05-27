package com.devsoft.rgdi_store.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.authentication.PasswordUtils;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

	//Injetando dependências
    private final ClienteRepository clienteRepository;
    private final PasswordUtils passwordUtils;

    public ClienteService(ClienteRepository repository, PasswordUtils passwordUtils) {
        this.clienteRepository = repository;
        this.passwordUtils = passwordUtils;
    }
    
    @Transactional(readOnly = true)
    public List<ClienteEntity> findAll() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ClienteEntity findClienteById(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }
    
    @Transactional(readOnly = true)
    public ClienteEntity findByIdComEnderecos(Long id) {
        return clienteRepository.findByIdComEnderecos(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }
    
    @Transactional
    public ClienteEntity saveClienteOnly(ClienteEntity cliente, String confirmaSenha) {
        // Validação do cliente
        ClienteValidationSaveService.validateCliente(cliente, clienteRepository, confirmaSenha);
        
        cliente.setSenha(passwordUtils.encrypt(cliente.getSenha())); // Criptografa a senha
        return clienteRepository.save(cliente); // salva no DB
    }  

    @Transactional
    public ClienteEntity update(Long id, ClienteEntity novo) {
        try {
            // Uso do lazy (Carrega preguiçosamente - quando necessário) - "getReferenceById"
        	ClienteEntity original = clienteRepository.getReferenceById(id);
            
            // Faz a validação apenas do nome
            ClienteValidationSaveService.validateName(novo.getNome());
            
            original.setNome(novo.getNome()); // recebe o novo nome
            original.setDataNascimento(novo.getDataNascimento()); // recebe a nova data de nascimento
            original.setGenero(novo.getGenero()); // recebe o novo genero

            return clienteRepository.save(original); // salva alteração no DB
        } catch (EntityNotFoundException e) { // Gera uma Exception
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update"); // Envia uma notificação
        }
    }
    
    @Transactional
    public ClienteEntity alterPassword(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        try {
        	// Uso do lazy
            ClienteEntity original = clienteRepository.getReferenceById(id);           

            // Verifica se a senha atual confere
            if (!passwordUtils.matches(senhaAtual, original.getSenha())) {
                throw new InvalidPassException("Senha atual incorreta.");
            }
            
            ClienteValidationSaveService.validatePassword(novaSenha, confirmaSenha); // Valida nova senha
            
            original.setSenha(passwordUtils.encrypt(novaSenha)); // Criptografa nova senha e atualiza

            return clienteRepository.save(original); // Salva alteração no DB
        } catch (EntityNotFoundException e) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update");
        }
    }

}

