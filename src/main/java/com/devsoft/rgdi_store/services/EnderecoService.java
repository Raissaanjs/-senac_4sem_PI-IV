package com.devsoft.rgdi_store.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.exceptions.all.EnderecoDuplicadoException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.repositories.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public EnderecoService(EnderecoRepository enderecoRepository,
    						ClienteService clienteService,
    						ClienteRepository clienteRepository,
    						PedidoRepository pedidoRepository) {
        this.enderecoRepository = enderecoRepository;
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<EnderecoEntity> findAll() {
        return enderecoRepository.findAll();
    }
    
    public Optional<EnderecoEntity> findById(Long id) {
        return enderecoRepository.findById(id);
    }

    
 // Método principal para salvar o endereço
    @Transactional
    public void saveEndereco(ClienteEntity cliente, EnderecoEntity endereco) {
        endereco.setCliente(cliente);

        if (endereco.getTipo() == EnderecoTipo.FATURAMENTO) {
            handleFaturamento(cliente, endereco);
        } else if (endereco.getTipo() == EnderecoTipo.ENTREGA) {
            handleEntrega(cliente, endereco);
        }
    }

    // Método para cadastrar novo endereço de FATURAMENTO
    private void handleFaturamento(ClienteEntity cliente, EnderecoEntity novoEndereco) {
        Optional<EnderecoEntity> enderecoExistente = enderecoRepository.findByClienteAndTipo(cliente, EnderecoTipo.FATURAMENTO);

        enderecoExistente.ifPresent(endAntigo -> {
            if (isEnderecoDuplicadoFaturamento(cliente, novoEndereco)) {
                // Endereço FATURAMENTO idêntico já existe — mostra mensagem e não salva
                throw new EnderecoDuplicadoException("Já existe um endereço de FATURAMENTO idêntico cadastrado.");
            } else {
                // Verifica se já existe um endereço de ENTREGA igual ao FATURAMENTO antigo
                boolean existeEntregaIgual = isEnderecoDuplicadoEntrega(cliente, endAntigo);

                if (existeEntregaIgual) {
                    // Se já existe ENTREGA idêntico, remove o FATURAMENTO antigo
                    enderecoRepository.delete(endAntigo);
                } else {
                    // Caso contrário, transforma o FATURAMENTO atual em ENTREGA
                    endAntigo.setTipo(EnderecoTipo.ENTREGA);
                    enderecoRepository.save(endAntigo);
                }
            }
        });

        // Salva o novo como FATURAMENTO
        enderecoRepository.save(novoEndereco);
    }


    // Método para cadastrar novo endereço de ENTREGA
    private void handleEntrega(ClienteEntity cliente, EnderecoEntity endereco) {
        // Verifica se já existe um endereço ENTREGA idêntico
        if (isEnderecoDuplicadoEntrega(cliente, endereco)) {
        	throw new EnderecoDuplicadoException("Já existe um endereço de ENTREGA idêntico cadastrado.");

        }

        // Se não houver duplicidade, salva o novo endereço de ENTREGA
        enderecoRepository.save(endereco);
    }
    

    //OK
    @Transactional
    public void tornarPrincipal(Long clienteId, Long novoPrincipalId) {
        ClienteEntity cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente com ID " + clienteId + " não encontrado"));

        List<EnderecoEntity> enderecos = enderecoRepository.findAllByClienteId(clienteId);

        EnderecoEntity atualPrincipal = enderecos.stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        EnderecoEntity novoPrincipal = enderecos.stream()
            .filter(e -> e.getId().equals(novoPrincipalId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Endereço com ID " + novoPrincipalId + " não encontrado"));

        // Troca o antigo FATURAMENTO para ENTREGA (caso necessário)
        if (atualPrincipal != null && !atualPrincipal.getId().equals(novoPrincipalId)) {
            boolean duplicado = isEnderecoDuplicadoEntrega(cliente, atualPrincipal);

            if (!duplicado) {
                atualPrincipal.setTipo(EnderecoTipo.ENTREGA);
                enderecoRepository.save(atualPrincipal);
            } else {
                // Se já existe ENTREGA idêntico, remove o FATURAMENTO antigo
                enderecoRepository.delete(atualPrincipal);
            }
        }

        // Se o novo endereço já é FATURAMENTO, não precisa mudar
        if (novoPrincipal.getTipo() == EnderecoTipo.FATURAMENTO) {
            return;
        }

        // Se tiver pedidos vinculados, duplicar o endereço
        if (hasPedidosVinculados(novoPrincipal)) {
            EnderecoEntity copia = new EnderecoEntity();
            copia.setCliente(cliente);
            copia.setTipo(EnderecoTipo.FATURAMENTO);
            copia.setCep(novoPrincipal.getCep());
            copia.setLogradouro(novoPrincipal.getLogradouro());
            copia.setNumero(novoPrincipal.getNumero());
            copia.setComplemento(novoPrincipal.getComplemento());
            copia.setBairro(novoPrincipal.getBairro());
            copia.setLocalidade(novoPrincipal.getLocalidade());
            copia.setUf(novoPrincipal.getUf());

            enderecoRepository.save(copia);
        } else {
            novoPrincipal.setTipo(EnderecoTipo.FATURAMENTO);
            enderecoRepository.save(novoPrincipal);
        }
    }

    
    // ========== MÉTODOS AUXILIARES ==========
    public ClienteEntity buscarClienteComEnderecos(Long clienteId) {
        return clienteService.findByIdComEnderecos(clienteId);
    }
    
    private boolean hasPedidosVinculados(EnderecoEntity endereco) {
        return pedidoRepository.existsByEndereco(endereco);
    }
    
    // Verifica se já existe um endereço duplicado para o tipo FATURAMENTO
    private boolean isEnderecoDuplicadoFaturamento(ClienteEntity cliente, EnderecoEntity endereco) {
        return cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .anyMatch(e ->
                Objects.equals(e.getCep(), endereco.getCep()) &&
                Objects.equals(e.getLogradouro(), endereco.getLogradouro()) &&
                Objects.equals(e.getNumero(), endereco.getNumero()) &&
                Objects.equals(e.getComplemento(), endereco.getComplemento()) &&
                Objects.equals(e.getBairro(), endereco.getBairro()) &&
                Objects.equals(e.getLocalidade(), endereco.getLocalidade()) &&
                Objects.equals(e.getUf(), endereco.getUf())
            );
    }
    
    // Verifica se já existe um endereço duplicado para o tipo ENTREGA
    private boolean isEnderecoDuplicadoEntrega(ClienteEntity cliente, EnderecoEntity endereco) {
        return cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .anyMatch(e ->
                e.getCep().equals(endereco.getCep()) &&
                e.getLogradouro().equals(endereco.getLogradouro()) &&
                e.getNumero().equals(endereco.getNumero()) &&
                Objects.equals(e.getComplemento(), endereco.getComplemento()) &&
                e.getBairro().equals(endereco.getBairro()) &&
                e.getLocalidade().equals(endereco.getLocalidade()) &&
                e.getUf().equals(endereco.getUf())
            );
    }
    

}

