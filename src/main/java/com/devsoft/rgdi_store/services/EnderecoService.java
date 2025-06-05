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
import com.devsoft.rgdi_store.exceptions.all.EnderecoNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.repositories.EnderecoRepository;
import com.devsoft.rgdi_store.repositories.PedidoRepository;

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

   // Método lista todos os endereços
    @Transactional(readOnly = true)
    public List<EnderecoEntity> findAll() {
        return enderecoRepository.findAll();
    }
    
    // Método busca endereço por ID
    @Transactional(readOnly = true)
    public Optional<EnderecoEntity> findById(Long id) {
        return enderecoRepository.findById(id);
    }
    
    // Método principal para salvar o endereço
    @Transactional
    public void saveEndereco(ClienteEntity cliente, EnderecoEntity endereco) {
        // Vincula o endereço ao cliente
    	endereco.setCliente(cliente);

    	// Se o endereço for do tipo FATURAMENTO
        if (endereco.getTipo() == EnderecoTipo.FATURAMENTO) {
            handleFaturamento(cliente, endereco); // Chama o método abaixo
        // Se o endereço for do tipo ENTREGA
        } else if (endereco.getTipo() == EnderecoTipo.ENTREGA) {
            handleEntrega(cliente, endereco); // Chama o método ao lado
        }
    }

    // Método para cadastrar novo endereço de FATURAMENTO
    private void handleFaturamento(ClienteEntity cliente, EnderecoEntity novoEndereco) {
    	// Verifica se já existe um endereço do tipo FATURAMENTO
        Optional<EnderecoEntity> enderecoExistente = enderecoRepository.findByClienteAndTipo(cliente, EnderecoTipo.FATURAMENTO);

        // Expressão lambda 'endAntigo ->{...}'. O bloco {...} só será executado se 'enderecoExistente' tiver um valor
        enderecoExistente.ifPresent(endAntigo -> {
        	// Verifica se já existe um endereço tipo FATURAMENTO idêntico
        	if (isEnderecoDuplicadoFaturamento(cliente, novoEndereco)) {
            	// Se já houver envia a mensagem abaixo
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

        // Salva o novo endereço com o tipo FATURAMENTO
        enderecoRepository.save(novoEndereco);
    }

    // Método para cadastrar novo endereço de ENTREGA
    private void handleEntrega(ClienteEntity cliente, EnderecoEntity endereco) {
        // Verifica se já existe um endereço tipo ENTREGA idêntico
        if (isEnderecoDuplicadoEntrega(cliente, endereco)) {
        	// Se já houver o endereço envia a mensagem abaixo
        	throw new EnderecoDuplicadoException("Já existe um endereço de ENTREGA idêntico cadastrado.");
        }

        // Se não houver duplicidade, salva o novo endereço de ENTREGA
        enderecoRepository.save(endereco);
    }   
    
    // Método para trocar endereço do tipo ENTREGA para tipo FATURAMENTO
    @Transactional
    public void tornarPrincipal(Long clienteId, Long novoPrincipalId) {
        ClienteEntity cliente = clienteRepository.findById(clienteId) // Busca cliente pelo ID
        	// Se não encontrar envia exceção
            .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente com ID " + clienteId + " não encontrado"));

        // Busca todos os endereços associados ao cliente
        List<EnderecoEntity> enderecos = enderecoRepository.findAllByClienteId(clienteId);

        // Filtra a lista de endereços para encontrar o endereço atual de faturamento
        EnderecoEntity atualPrincipal = enderecos.stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null); //Se não encontrar nenhum, retorna null

        // Procura, entre os endereços do cliente, aquele com o ID igual ao novoPrincipalId
        EnderecoEntity novoPrincipal = enderecos.stream()
            .filter(e -> e.getId().equals(novoPrincipalId))
            .findFirst()
            // Se não encontrar, lança a exceção abaixo
            .orElseThrow(() -> new EnderecoNaoEncontradoException("Endereço com ID " + novoPrincipalId + " não encontrado"));

        // Troca o antigo FATURAMENTO para ENTREGA (caso necessário)
        if (atualPrincipal != null && !atualPrincipal.getId().equals(novoPrincipalId)) {
        	// Chama o método auxiliar "isEnderecoDuplicadoEntrega"
        	boolean duplicado = isEnderecoDuplicadoEntrega(cliente, atualPrincipal);

        	// Se não estiver duplicado
            if (!duplicado) {
            	// Define o antigo FATURAMENTO como tipo ENTREGA
                atualPrincipal.setTipo(EnderecoTipo.ENTREGA);
                // Salva o endereço com a nova alteração
                enderecoRepository.save(atualPrincipal);
            } else {
                // Se já existe ENTREGA idêntico, remove o FATURAMENTO antigo
                enderecoRepository.delete(atualPrincipal);
            }
        }

        // Se o novo endereço já é tipo FATURAMENTO, não faz nada
        if (novoPrincipal.getTipo() == EnderecoTipo.FATURAMENTO) {
            return;
        }

        // Troca o antigo ENTREGA para FATURAMENTO
        // Se o endereço tipo ENTREGA tiver algum pedido vinculado, duplica o endereço - PROCESSAMENTO EM MEMÓRIA
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

            // Salva uma cópia do endereço tipo ENTREGA no FATUREMENTO
            enderecoRepository.save(copia);
        } else {
        	// Senão altera o tipo do endereço de ENTREGA para tipo FATURAMENTO
            novoPrincipal.setTipo(EnderecoTipo.FATURAMENTO);
            enderecoRepository.save(novoPrincipal);
        }
    }

    
    // ========== MÉTODOS AUXILIARES ==========
    // Método busca cliente onde tenha ID vinculado ao endereço
    @Transactional(readOnly = true)
    public ClienteEntity buscarClienteComEnderecos(Long clienteId) {
        return clienteService.findByIdComEnderecos(clienteId);
    }
    
    // Método verifica se já existe pedido vinculado ao endereço
    private boolean hasPedidosVinculados(EnderecoEntity endereco) {
        return pedidoRepository.existsByEndereco(endereco);
    }
    
    // Verifica se já existe um endereço duplicado para o tipo FATURAMENTO
    private boolean isEnderecoDuplicadoFaturamento(ClienteEntity cliente, EnderecoEntity endereco) {
        
    	return cliente.getEnderecos().stream() // Converte em .stream para aplicar filtro
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO) // Filtra apenas endereço tipo FATURAMENTO
            .anyMatch(e -> // Verifica se algum dos endereços filtrados coincide com o endereço passado 'endereco' 
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
       
    	return cliente.getEnderecos().stream()  // Converte em .stream para aplicar filtro
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA) // Filtra apenas endereço tipo ENTREGA
            .anyMatch(e -> // Verifica se algum dos endereços filtrados coincide com o endereço passado 'endereco'
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

