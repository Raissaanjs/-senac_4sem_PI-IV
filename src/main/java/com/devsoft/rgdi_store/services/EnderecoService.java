package com.devsoft.rgdi_store.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public void saveEndereco(ClienteEntity cliente, EnderecoEntity endereco) {
        endereco.setCliente(cliente);

        Optional<EnderecoEntity> enderecoExistente = enderecoRepository.findByClienteAndTipo(cliente, endereco.getTipo());
        if (enderecoExistente.isPresent()) {
            EnderecoEntity enderecoAtual = enderecoExistente.get();
            atualizarEndereco(enderecoAtual, endereco);
            enderecoRepository.save(enderecoAtual);
        } else {
            enderecoRepository.save(endereco);
        }
    }

    private void atualizarEndereco(EnderecoEntity enderecoExistente, EnderecoEntity novoEndereco) {
        enderecoExistente.setCep(novoEndereco.getCep());
        enderecoExistente.setLogradouro(novoEndereco.getLogradouro());
        enderecoExistente.setNumero(novoEndereco.getNumero());
        enderecoExistente.setComplemento(novoEndereco.getComplemento());
        enderecoExistente.setBairro(novoEndereco.getBairro());
        enderecoExistente.setLocalidade(novoEndereco.getLocalidade());
        enderecoExistente.setUf(novoEndereco.getUf());
    }
    
    
}


