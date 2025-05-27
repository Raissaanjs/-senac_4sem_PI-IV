package com.devsoft.rgdi_store.utility;

import org.springframework.stereotype.Component;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;

@Component
public class ClienteAutenticadoHelper {

    private final ClienteRepository repository;

    public ClienteAutenticadoHelper(ClienteRepository repository) {
        this.repository = repository;
    }

    public ClienteEntity getClienteLogado(String email) {
        return repository.findByEmail(email)
        		.orElseThrow(() -> new ClienteNaoEncontradoException("Cliente com e-mail " + email + " não encontrado"));

    }
}

