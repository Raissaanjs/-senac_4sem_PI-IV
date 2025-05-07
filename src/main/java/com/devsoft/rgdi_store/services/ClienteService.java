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

    @Transactional
    public ClienteEntity findClienteById(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }
    
    public ClienteEntity findByIdComEnderecos(Long id) {
        return clienteRepository.findByIdComEnderecos(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado com ID: " + id));
    }
    
    @Transactional
    public ClienteEntity saveClienteOnly(ClienteEntity cliente, String confirmaSenha) {
        // Validação do cliente (pode ser simplificada aqui se já foi feita no controller)
        ClienteValidationSaveService.validateCliente(cliente, clienteRepository, confirmaSenha);

        // Criptografando a senha
        cliente.setSenha(passwordUtils.encrypt(cliente.getSenha()));
        return clienteRepository.save(cliente);
    }  

    @Transactional
    public ClienteEntity update(Long id, ClienteEntity novo) {
        try {
            ClienteEntity original = clienteRepository.getReferenceById(id);
            
            if (original == null) {
                throw new ClienteNaoEncontradoException("Cliente não encontrado para atualização");
            }
            
            // Validação apenas do nome
            ClienteValidationSaveService.validateName(novo.getNome());
            
            original.setNome(novo.getNome());
            original.setDataNascimento(novo.getDataNascimento());
            original.setGenero(novo.getGenero());

            return clienteRepository.save(original);
        } catch (EntityNotFoundException e) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update");
        }
    }
    
    @Transactional
    public ClienteEntity alterPassword(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        try {
            ClienteEntity original = clienteRepository.getReferenceById(id);

            if (original == null) {
                throw new ClienteNaoEncontradoException("Cliente não encontrado para atualização");
            }

            // Verifica se a senha atual confere
            if (!passwordUtils.matches(senhaAtual, original.getSenha())) {
                throw new InvalidPassException("Senha atual incorreta.");
            }

            // Valida nova senha
            ClienteValidationSaveService.validatePassword(novaSenha, confirmaSenha);

            // Criptografa nova senha e atualiza
            original.setSenha(passwordUtils.encrypt(novaSenha));

            return clienteRepository.save(original);
        } catch (EntityNotFoundException e) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update");
        }
    }

}

