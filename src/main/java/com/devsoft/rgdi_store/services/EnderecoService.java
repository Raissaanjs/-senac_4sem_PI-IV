package com.devsoft.rgdi_store.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;

@Service
public class EnderecoService {

    private final EnderecoRepository repository;

    public EnderecoService(EnderecoRepository repository) {
        this.repository = repository;
    }

    /*
    @Transactional
    public EnderecoEntity insert(EnderecoEntity endereco) {
        validarEnderecoObrigatorio(endereco);
        return repository.save(endereco);
    }
    */

    private void validarEnderecoObrigatorio(EnderecoEntity endereco) {
        if (endereco.getTipo() == null || 
           (endereco.getTipo() != EnderecoTipo.FATURAMENTO && endereco.getTipo() != EnderecoTipo.ENTREGA)) {
            throw new IllegalArgumentException("Endereço deve ser do tipo FATURAMENTO ou ENTREGA.");
        }

        if (isBlank(endereco.getCep()) ||
            isBlank(endereco.getLogradouro()) ||
            endereco.getNumero() == null ||
            isBlank(endereco.getBairro()) ||
            isBlank(endereco.getCidade()) ||
            isBlank(endereco.getUf())) {
            throw new IllegalArgumentException("Todos os campos obrigatórios devem ser preenchidos no endereço " + endereco.getTipo());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}


