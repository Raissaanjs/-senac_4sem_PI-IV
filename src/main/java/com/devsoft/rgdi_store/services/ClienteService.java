package com.devsoft.rgdi_store.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.authentication.PasswordUtils;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordUtils passwordUtils;

    public ClienteService(ClienteRepository repository, PasswordUtils passwordUtils, EnderecoRepository enderecoRepository) {
        this.repository = repository;
        this.passwordUtils = passwordUtils;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional(readOnly = true)
    public ClienteEntity findClienteById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    @Transactional
    public ClienteEntity saveClienteOnly(ClienteEntity cliente) {
        // Validação do cliente (pode ser simplificada aqui se já foi feita no controller)
        ClienteValidationSaveService.validateCliente(cliente, repository);

        // Criptografando a senha
        cliente.setSenha(passwordUtils.encrypt(cliente.getSenha()));
        return repository.save(cliente);
    }

    @Transactional
    public void saveEnderecos(Long clienteId, EnderecoEntity enderecoFaturamento, EnderecoEntity enderecoEntrega) {
        ClienteEntity cliente = repository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);
        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);

        enderecoFaturamento.setCliente(cliente);
        enderecoEntrega.setCliente(cliente);

        enderecoRepository.save(enderecoFaturamento);
        enderecoRepository.save(enderecoEntrega);
    }

    @Transactional
    public ClienteEntity update(Long id, ClienteEntity novo) {
        try {
            ClienteEntity original = repository.getReferenceById(id);
            original.setNome(novo.getNome());
            original.setDataNascimento(novo.getDataNascimento());
            original.setGenero(novo.getGenero());

            if (novo.getSenha() != null && !novo.getSenha().isEmpty()) {
                original.setSenha(passwordUtils.encrypt(novo.getSenha()));
            }

            return repository.save(original);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Cliente não encontrado para update");
        }
    }
}

