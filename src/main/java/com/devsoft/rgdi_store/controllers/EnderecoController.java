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
import com.devsoft.rgdi_store.exceptions.all.ClienteNaoEncontradoException;
import com.devsoft.rgdi_store.exceptions.all.EnderecoDuplicadoException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/enderecos") // Define a URL base para todas as solicitações deste controlador
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
    	// Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoFaturamento", new EnderecoEntity());
        model.addAttribute("enderecoEntrega", new EnderecoEntity());
        
        return "endereco/cadendereco"; // View que retorna o cadastro de endereço
    }
	    
    // Cadastro endereço Inicial - Faturamento
    @PostMapping("/noauth/endereco-inicial/faturamento")
    public ResponseEntity<Map<String, Long>> salvarEnderecoFaturamentoInicial(
            @RequestParam("clienteId") Long clienteId,
            @ModelAttribute("enderecoFaturamento") EnderecoEntity enderecoFaturamento) {

    	// Define explicitamente o tipo do endereço como FATURAMENTO
        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO);

        // Recupera o cliente correspondente ao clienteId informado
        ClienteEntity cliente = buscarClienteComEnderecos(clienteId);
        
        // Salva o endereço no banco de dados
        enderecoService.saveEndereco(cliente, enderecoFaturamento);

        // Retorna um HTTP 200 OK, sucesso
        return ResponseEntity.ok(Collections.singletonMap("clienteId", clienteId));
    }

    // Cadastro endereço Inicial - Entrega
    @PostMapping("/noauth/endereco-inicial/entrega")
    public String salvarEnderecoEntrega(@RequestParam("clienteId") Long clienteId,
                                        @ModelAttribute("enderecoEntrega") EnderecoEntity enderecoEntrega) {

    	// Define explicitamente o tipo do endereço como ENTREGA
        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA);

        // Recupera o cliente correspondente ao clienteId informado
        ClienteEntity cliente = buscarClienteComEnderecos(clienteId);
        
        // Salva o endereço no banco de dados
        enderecoService.saveEndereco(cliente, enderecoEntrega);

        // Redireciona para o endpoint que chama a View que retorna a página de login do cliente
        return "redirect:/clientes/login";
    }
    
    // Abre o form de cadastro para adicionar novo endereço - Em Meus Dados
    @GetMapping("/auth/meusdados/novo")
    public String novoEndereco(Model model, Principal principal) {
    	// Recupera o cliente autenticado
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

    	// Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", new EnderecoEntity()); // novo objeto limpo
        return "endereco/cadendereconovo"; // View que retorna a página de cadastro de novo endereço
    }
    
    @PostMapping("/auth/meusdados/salvar")
    public String salvarEndereco(
            @ModelAttribute("endereco") EnderecoEntity endereco,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
    	// Recupera o cliente autenticado
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        try {
            // Garante a associação com o cliente
            endereco.setCliente(cliente);

            // Validação de tipo: você pode validar se foi enviado corretamente
            if (endereco.getTipo() == null) {
                redirectAttributes.addFlashAttribute("erro", "Tipo de endereço é obrigatório."); // envia msg de erro para view abaixo
                return "redirect:/enderecos/auth/meusdados/novo"; // Redireciona para view de cadastro de novo endereço
            }

            enderecoService.saveEndereco(cliente, endereco); // método que lida com ENTREGA/FATURAMENTO

            // Envia msg de sucesso para view abaixo
            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço (Meus Dados) criado com sucesso!");
            // View que retorna a página de detalhes do cliente, com a mensagem acima
            return "redirect:/clientes/auth/detalhes-cliente?sucesso"; 

        } catch (EnderecoDuplicadoException e) {
        	// Envia msg de erro para view abaixo
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            // Redireciona para view de cadastro de novo endereço 
            return "redirect:/enderecos/auth/meusdados/novo";
        }
    }
    
    @GetMapping("/auth/endereco-entrega/listar")
    public String listarEnderecosEntrega(Model model, Principal principal) {
    	// Recupera o cliente autenticado
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());
        
        // Traz uma lista de endereços de Entrega
        List<EnderecoEntity> enderecosEntrega = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Adiciona os dados ao Model para ser mostrado na View 
        model.addAttribute("enderecosEntrega", enderecosEntrega);

        return "pedido/pedido-endereco-entrega"; // View que retorna a página para selecionar endereço de entrega
    }
    
    //Abre form para cadastrar novo endereço de entrega - Fase de fechamento de pedido
    @GetMapping("/auth/endereco-entrega/pedido/novo")
    public String novoEnderecoEntrega(Model model, Principal principal) {
    	// Recupera o cliente autenticado
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Cria um novo objeto EnderecoEntity já com tipo ENTREGA
        EnderecoEntity novoEndereco = new EnderecoEntity();
        novoEndereco.setTipo(EnderecoTipo.ENTREGA); // Define o tipo como ENTREGA

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("clienteId", cliente.getId());
        model.addAttribute("endereco", novoEndereco);

        return "endereco/cadendereconovoentrega"; // View que retorna a página de cadastro de novo endereço de entrega
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

            // Salva o endereço
            enderecoService.saveEndereco(cliente, endereco);

            // Envia msg de sucesso para view abaixo
            redirectAttributes.addFlashAttribute("sucesso", "Novo Endereço de Entrega criado com sucesso!");
            // Redireciona para View que retorna a página para selecionar endereço de entrega
            return "redirect:/enderecos/auth/endereco-entrega/listar";

        } catch (ClienteNaoEncontradoException e) {
        	// Envia msg de erro para view abaixo
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
            // Redireciona para o "endpoint" que chama a View de cadastro de novo endereço de entrega
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";

        } catch (EnderecoDuplicadoException e) {
        	// Envia msg de erro para view abaixo
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            // Redireciona para o "endpoint" que chama a View de cadastro de novo endereço de entrega
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";

        } catch (Exception e) {
        	// Envia msg de erro para view abaixo
        	redirectAttributes.addFlashAttribute("erro", "Erro ao salvar endereço de entrega: " + e.getMessage());
            // Redireciona para o "endpoint" que chama a View de cadastro de novo endereço de entrega
            return "redirect:/enderecos/auth/endereco-entrega/pedido/novo";
        }
    }
    
    @PostMapping("/auth/mudar-principal/{clienteId}/{novoPrincipalId}")
    public String mudarEnderecoPrincipal(@PathVariable Long clienteId,
                                         @PathVariable Long novoPrincipalId,
                                         RedirectAttributes redirectAttributes) {
        try {
        	// Acessa o service para mudar endereço de ENTREGA para FATURAMENTO
            enderecoService.tornarPrincipal(clienteId, novoPrincipalId);
            // 	Envia msg de sucesso para view abaixo
            redirectAttributes.addFlashAttribute("sucesso", "Endereço principal atualizado com sucesso!");
        } catch (ClienteNaoEncontradoException e) {
        	// Envia msg de erro para view abaixo
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado: " + e.getMessage());
        } catch (EntityNotFoundException e) {
        	// Envia msg de erro para view abaixo
        	redirectAttributes.addFlashAttribute("erro", "Endereço inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            // Envia msg de erro para a View abaixo
        	redirectAttributes.addFlashAttribute("erro", "Erro inesperado: " + e.getMessage());
        }

        // Redireciona para o "endpoint" que chama a View de detalhes do cliente
        return "redirect:/clientes/auth/detalhes/" + clienteId; 
    }
 
    
    // ========== MÉTODO(S) AUXILIAR(ES) ==========
    // Busca Cliente com endereço vinculado ao seu ID 
    public ClienteEntity buscarClienteComEnderecos(Long clienteId) {
        return clienteService.findByIdComEnderecos(clienteId);
    } 
}
