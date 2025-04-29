package com.devsoft.rgdi_store.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.authentication.PasswordUtils;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.exceptions.All.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidPassException;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

	//Injetando dependências
    private final ClienteRepository repository;
    private final EnderecoService enderecoService;
    private final PasswordUtils passwordUtils;

    public ClienteService(ClienteRepository repository, PasswordUtils passwordUtils, EnderecoService enderecoService) {
        this.repository = repository;
        this.passwordUtils = passwordUtils;
        this.enderecoService = enderecoService;
    }
    
    @Transactional(readOnly = true)
    public List<ClienteEntity> findAll() {
        return repository.findAll();
    }

    @Transactional
    public ClienteEntity findClienteById(Long clienteId) {
        return repository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
    }
    
    
    @Transactional
    public ClienteEntity saveClienteOnly(ClienteEntity cliente, String confirmaSenha) {
        // Validação do cliente (pode ser simplificada aqui se já foi feita no controller)
        ClienteValidationSaveService.validateCliente(cliente, repository, confirmaSenha);

        // Criptografando a senha
        cliente.setSenha(passwordUtils.encrypt(cliente.getSenha()));
        return repository.save(cliente);
    }    
    
    //salva endereço individual
    @Transactional
    public void saveEndereco(Long clienteId, EnderecoEntity endereco) {
        ClienteEntity cliente = repository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
        endereco.setCliente(cliente);
        enderecoService.saveEndereco(cliente, endereco);
    }

    @Transactional
    public void saveEnderecos(Long clienteId, EnderecoEntity enderecoFaturamento, EnderecoEntity enderecoEntrega) {
        ClienteEntity cliente = repository.findById(clienteId)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));

        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);
        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);

        enderecoService.saveEndereco(cliente, enderecoFaturamento);
        enderecoService.saveEndereco(cliente, enderecoEntrega);
    }

    @Transactional
    public ClienteEntity update(Long id, ClienteEntity novo) {
        try {
            ClienteEntity original = repository.getReferenceById(id);
            
            if (original == null) {
                throw new ClienteNaoEncontradoException("Cliente não encontrado para atualização");
            }
            
            // Validação apenas do nome
            ClienteValidationSaveService.validateName(novo.getNome());
            
            original.setNome(novo.getNome());
            original.setDataNascimento(novo.getDataNascimento());
            original.setGenero(novo.getGenero());

            return repository.save(original);
        } catch (EntityNotFoundException e) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update");
        }
    }
    
    @Transactional
    public ClienteEntity alterPassword(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        try {
            ClienteEntity original = repository.getReferenceById(id);

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

            return repository.save(original);
        } catch (EntityNotFoundException e) {
            throw new ClienteNaoEncontradoException("Cliente não encontrado para update");
        }
    }

}

