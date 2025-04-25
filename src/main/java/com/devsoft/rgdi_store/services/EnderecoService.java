package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.services.exceptions.EnderecoDuplicadoException;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public void saveEndereco(ClienteEntity cliente, EnderecoEntity novoEndereco) {
        novoEndereco.setCliente(cliente);

        if (novoEndereco.getTipo() == EnderecoTipo.FATURAMENTO) {
            handleFaturamento(cliente, novoEndereco);
        } else if (novoEndereco.getTipo() == EnderecoTipo.ENTREGA) {
            handleEntrega(cliente, novoEndereco);
        }
    }

    private void handleFaturamento(ClienteEntity cliente, EnderecoEntity novoEndereco) {
        Optional<EnderecoEntity> atualFaturamento = enderecoRepository.findByClienteAndTipo(cliente, EnderecoTipo.FATURAMENTO);

        if (atualFaturamento.isPresent()) {
            EnderecoEntity antigo = atualFaturamento.get();

            // 1. Muda tipo para ENTREGA e força flush no banco
            antigo.setTipo(EnderecoTipo.ENTREGA);
            enderecoRepository.saveAndFlush(antigo); // 🔥 Isso é crucial!

            // 2. Busca duplicado atualizado diretamente do banco
            Optional<EnderecoEntity> duplicado = enderecoRepository.findAllByClienteId(cliente.getId()).stream()
                .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA && !e.getId().equals(antigo.getId()))
                .filter(e -> isEnderecoIgual(e, antigo))
                .findFirst();

            duplicado.ifPresent(enderecoRepository::delete);
        }

        // 3. Agora é seguro salvar como FATURAMENTO
        enderecoRepository.save(novoEndereco);
    }


    private void handleEntrega(ClienteEntity cliente, EnderecoEntity novoEndereco) {
        if (isEnderecoDuplicado(cliente, novoEndereco)) {
            throw new EnderecoDuplicadoException("Já existe um endereço de ENTREGA idêntico cadastrado.");
        }

        enderecoRepository.save(novoEndereco);
    }

    private boolean isEnderecoDuplicado(ClienteEntity cliente, EnderecoEntity endereco) {
        return cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .anyMatch(e -> isEnderecoIgual(e, endereco));
    }

    private boolean isEnderecoIgual(EnderecoEntity e1, EnderecoEntity e2) {
        return e1.getCep().equals(e2.getCep()) &&
               e1.getLogradouro().equals(e2.getLogradouro()) &&
               e1.getNumero().equals(e2.getNumero()) &&
               Objects.equals(e1.getComplemento(), e2.getComplemento()) &&
               e1.getBairro().equals(e2.getBairro()) &&
               e1.getLocalidade().equals(e2.getLocalidade()) &&
               e1.getUf().equals(e2.getUf());
    }

    @Transactional
    public void tornarPrincipal(Long clienteId, Long novoPrincipalId) {
        List<EnderecoEntity> enderecos = enderecoRepository.findAllByClienteId(clienteId);

        EnderecoEntity atualPrincipal = enderecos.stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        EnderecoEntity novoPrincipal = enderecos.stream()
            .filter(e -> e.getId().equals(novoPrincipalId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Endereço de entrega não encontrado"));

        if (atualPrincipal != null) {
            atualPrincipal.setTipo(EnderecoTipo.ENTREGA);
            enderecoRepository.save(atualPrincipal);

            Optional<EnderecoEntity> duplicado = enderecos.stream()
                .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA && !e.getId().equals(atualPrincipal.getId()))
                .filter(e -> isEnderecoIgual(e, atualPrincipal))
                .findFirst();

            duplicado.ifPresent(enderecoRepository::delete);
        }

        novoPrincipal.setTipo(EnderecoTipo.FATURAMENTO);
        enderecoRepository.save(novoPrincipal);
    }
}




