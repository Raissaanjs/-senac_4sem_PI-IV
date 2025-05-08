package com.devsoft.rgdi_store.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.exceptions.EnderecoDuplicadoException;
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/enderecos")
public class EnderecoController {
	
	private final ClienteService clienteService;
    private final EnderecoService enderecoService;
    private final ClienteAutenticadoHelper clienteHelper;    
    
    public EnderecoController(ClienteService clienteService, ClienteRepository clienteRepository,
    						 EnderecoService enderecoService, ClienteAutenticadoHelper clienteHelper) {
    	this.clienteService = clienteService;
    	this.enderecoService =enderecoService;
    	this.clienteHelper = clienteHelper;
    }
    
    @GetMapping("/noauth/cadastrar-endereco/{clienteId}")
    public String mostrarFormularioEnderecos(@PathVariable Long clienteId, Model model) {
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoFaturamento", new EnderecoEntity());
        model.addAttribute("enderecoEntrega", new EnderecoEntity());
        return "endereco/cadendereco";
    }
	    
    // Cadastro endereço Inicial - Faturamento
    @PostMapping("/noauth/endereco-inicial/faturamento")
    public ResponseEntity<Map<String, Long>> salvarEnderecoFaturamentoInicial(
            @RequestParam("clienteId") Long clienteId,
            @ModelAttribute("enderecoFaturamento") EnderecoEntity enderecoFaturamento) {

        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);

        ClienteEntity cliente = buscarClienteComEnderecos(clienteId);
        enderecoService.saveEndereco(cliente, enderecoFaturamento);

        return ResponseEntity.ok(Collections.singletonMap("clienteId", clienteId));
    }

    // Cadastro endereço Inicial - Entrega
    @PostMapping("/noauth/endereco-inicial/entrega")
    public String salvarEnderecoEntrega(@RequestParam("clienteId") Long clienteId,
                                        @ModelAttribute("enderecoEntrega") EnderecoEntity enderecoEntrega) {

        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);

        ClienteEntity cliente = buscarClienteComEnderecos(clienteId); // ou findById
        enderecoService.saveEndereco(cliente, enderecoEntrega);

        return "redirect:/clientes/login";
    }
    
    // Abre o form de cadastro para adicionar novo endereço - Em Meus Dados
    @GetMapping("/auth/meusdados/novo")
    public String novoEndereco(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", new EnderecoEntity()); // novo objeto limpo
        return "endereco/cadendereconovo";
    }
    
    @PostMapping("/auth/meusdados/salvar")
    public String salvarEndereco(
            @ModelAttribute("endereco") EnderecoEntity endereco,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        try {
            // Garante a associação com o cliente
            endereco.setCliente(cliente);

            // Validação de tipo: você pode validar se foi enviado corretamente
            if (endereco.getTipo() == null) {
                redirectAttributes.addFlashAttribute("erro", "Tipo de endereço é obrigatório.");
                return "redirect:/enderecos/auth/meusdados/novo";
            }

            enderecoService.saveEndereco(cliente, endereco); // método que lida com ENTREGA/FATURAMENTO

            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço (Meus Dados) criado com sucesso!");
            return "redirect:/clientes/auth/detalhes-cliente?sucesso";

        } catch (EnderecoDuplicadoException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/enderecos/auth/meusdados/novo";
        }
    }
    
    @GetMapping("/auth/endereco-entrega/listar")
    public String listarEnderecosEntrega(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        List<EnderecoEntity> enderecosEntrega = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        model.addAttribute("enderecosEntrega", enderecosEntrega);

        return "pedido/pedido-endereco-entrega"; // Nome da view Thymeleaf
    }
    
    //Abre form para cadastrar novo endereço de entrega - Fase de fechamento de pedido
    @GetMapping("/auth/endereco-entrega/pedido/novo")
    public String novoEnderecoEntrega(Model model, Principal principal) {
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Cria um novo objeto EnderecoEntity já com tipo ENTREGA
        EnderecoEntity novoEndereco = new EnderecoEntity();
        novoEndereco.setTipo(EnderecoTipo.ENTREGA); // Define o tipo como ENTREGA

        // Adiciona o ID do cliente e o objeto EnderecoEntity ao modelo
        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", novoEndereco); // O modelo receberá o objeto novoEndereco

        return "cliente/cadendereconovoentrega"; // O template Thymeleaf que exibe o formulário
    }
    
    @PostMapping("/auth/endereco-entrega/pedido/salvar")
    public String salvarEnderecoEntrega(
            @RequestParam("clienteId") Long clienteId,
            @ModelAttribute("endereco") EnderecoEntity endereco,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Busca o cliente com os endereços (como o service espera)
        	ClienteEntity cliente = buscarClienteComEnderecos(clienteId);

            // Garante o tipo do endereço como ENTREGA
            endereco.setTipo(EnderecoTipo.ENTREGA);

            // Usa o método base do service
            enderecoService.saveEndereco(cliente, endereco);

            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço de Entrega criado com sucesso!");
            return "redirect:/enderecos/auth/endereco-entrega/listar";

        } catch (ClienteNaoEncontradoException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";

        } catch (EnderecoDuplicadoException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar endereço de entrega: " + e.getMessage());
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";
        }
    }
    
    @PostMapping("/auth/mudar-principal/{clienteId}/{novoPrincipalId}")
    public String mudarEnderecoPrincipal(@PathVariable Long clienteId,
                                         @PathVariable Long novoPrincipalId,
                                         RedirectAttributes redirectAttributes) {
        try {
            enderecoService.tornarPrincipal(clienteId, novoPrincipalId);
            redirectAttributes.addFlashAttribute("sucesso", "Endereço principal atualizado com sucesso!");
        } catch (ClienteNaoEncontradoException e) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("erro", "Endereço inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado: " + e.getMessage());
        }

        return "redirect:/clientes/auth/detalhes/" + clienteId;
    }
 
    
    // ========== MÉTODO(S) AUXILIAR(ES) ==========
    public ClienteEntity buscarClienteComEnderecos(Long clienteId) {
        return clienteService.findByIdComEnderecos(clienteId);
    } 
}
