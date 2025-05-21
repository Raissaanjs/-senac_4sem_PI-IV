package com.devsoft.rgdi_store.controllers;

import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.dto.AlterPassDTO;
import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.exceptions.all.ConfirmPassNullException;
import com.devsoft.rgdi_store.exceptions.all.CpfExistsException;
import com.devsoft.rgdi_store.exceptions.all.EmailDivergException;
import com.devsoft.rgdi_store.exceptions.all.EmailExistsException;
import com.devsoft.rgdi_store.exceptions.all.InvalidCpfException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.exceptions.all.NameValidationException;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.EnderecoService;
import com.devsoft.rgdi_store.utility.ClienteAutenticadoHelper;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
	
    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final ClienteAutenticadoHelper clienteHelper;
    //injeção de dependência via construtor
    public ClienteController(ClienteService clienteService, ClienteRepository clienteRepository,
    						 EnderecoService enderecoService, ClienteAutenticadoHelper clienteHelper) {
    	this.clienteService = clienteService;
    	this.clienteRepository = clienteRepository;
    	this.clienteHelper = clienteHelper;
    }
    
    @GetMapping("/login")
    public String loginCliente(@RequestParam(value = "error", required = false) String error,
                               Model model) {
        if (error != null) {
            model.addAttribute("erro", "Dados inválidos. Tente novamente!");
        }
        return "login-cliente";
    }
    
    @GetMapping("/logout")
    public String logoutCliente() {
    	return "index";
    }
    
    @GetMapping("/noauth/cadastrar")
    public String cadastrarCliente(Model model) {
        model.addAttribute("cliente", new ClienteEntity()); // envia um form vazio para View abaixo
        return "/cliente/cadcliente";
    } 	
 	
 	@PostMapping("/noauth/salvar-cliente")
    public String salvarCliente(@ModelAttribute("cliente") ClienteEntity cliente,
                                @RequestParam("confirmaSenha") String confirmaSenha,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // Valida os campos do cliente, incluindo a confirmação da senha
            ClienteValidationSaveService.validateCliente(cliente, clienteRepository, confirmaSenha);
        } catch (NameValidationException e) {
            result.rejectValue("nome", "error.nome", e.getMessage());
        } catch (InvalidCpfException | CpfExistsException e) {
            result.rejectValue("cpf", "error.cpf", e.getMessage());
        } catch (EmailDivergException | EmailExistsException e) {
            result.rejectValue("email", "error.email", e.getMessage());
        } catch (InvalidPassException | ConfirmPassNullException e) {
            result.rejectValue("senha", "error.senha", e.getMessage());
            model.addAttribute("confirmaSenhaError", e.getMessage());
        }

        if (result.hasErrors()) { // Se encontrar erros
            model.addAttribute("cliente", cliente); // Mantém os dados preenchidos

            // Constrói uma mensagem geral com os erros dos campos
            StringBuilder mensagemErro = new StringBuilder("");
            for (FieldError erroCampo : result.getFieldErrors()) {
                mensagemErro.append(" ").append(erroCampo.getDefaultMessage()).append(";");
            }

            model.addAttribute("erro", mensagemErro.toString()); // Envia mensagem para View abaixo
            return "cliente/cadcliente";
        }

        // Define o grupo de usuário, caso não tenha sido atribuído
        if (cliente.getGrupo() == null) {
            cliente.setGrupo(UserGroup.ROLE_CLIENT);
        }

        try {
        	// Salva o cliente no DB
            ClienteEntity savedCliente = clienteService.saveClienteOnly(cliente, confirmaSenha);
            // Envia o clienteId para próxima etapa
            redirectAttributes.addAttribute("clienteId", savedCliente.getId());
            // redireciona para próxima etapa: Cadastro do endereço
            return "redirect:/enderecos/noauth/cadastrar-endereco/{clienteId}";
        } catch (Exception e) { // Se houver Exception
            model.addAttribute("erro", "Erro ao salvar cliente. Tente novamente."); // Envia mensagem para View abaixo
            return "cliente/cadcliente";
        }
    }
 	
 	@GetMapping("/auth/admin")
    public String posLoginCliente() {
        return "home-cliente"; // Renderiza o template home-admin.html
    }
 	
 	@GetMapping("/auth/detalhes/{id}")
    public String detalhesCliente(@PathVariable Long id, Model model) {
 		// Busca o cliente vinculado ao ID
 		ClienteEntity cliente = clienteService.findClienteById(id);
        
        // endereço de faturamento - Apenas 1
        EnderecoEntity faturamento = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        // Vários de entrega
        List<EnderecoEntity> entregas = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("cliente", cliente);
        model.addAttribute("enderecoFaturamento", faturamento);
        model.addAttribute("enderecosEntrega", entregas);
        return "cliente/detalhes"; // View que retorna a página de detalhes do cliente
    }
    
    @GetMapping("/auth/detalhes-cliente")
    public String detalhesClienteLogado(Principal principal, Model model) {    	
    	// Recupera o cliente autenticado
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        // Carrega o endereço de faturamento (pode ser o primeiro do tipo FATURAMENTO)
        EnderecoEntity faturamento = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.FATURAMENTO)
            .findFirst()
            .orElse(null);

        // Carrega o(s) endereço(s) de entrega
        List<EnderecoEntity> entregas = cliente.getEnderecos().stream()
            .filter(e -> e.getTipo() == EnderecoTipo.ENTREGA)
            .toList();

        // Adiciona os dados ao Model para ser mostrado na View
        model.addAttribute("cliente", cliente);
        model.addAttribute("enderecoFaturamento", faturamento);
        model.addAttribute("enderecosEntrega", entregas);
        return "cliente/detalhes"; // View que retorna a página de detalhes do cliente
    }     
   
    @GetMapping("/auth/editar")
    public String editarCliente(Model model, Principal principal) {
    	// Recupera o cliente autenticado
    	ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente); // Adiciona ao Model para ser mostrado na View abaixo
        return "cliente/editcliente";
    }

    @PostMapping("/auth/update")
    public String updateCliente(
            @ModelAttribute("cliente") ClienteEntity cliente,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
        	// Recupera o cliente autenticado
            ClienteEntity clienteLogado = clienteHelper.getClienteLogado(principal.getName());

            clienteService.update(clienteLogado.getId(), cliente); //atualiza o cliente

            // Mensagem de sucesso para o endpoint abaixo
            redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso!"); 
            return "redirect:/clientes/auth/detalhes-cliente?sucesso";
        } catch (NameValidationException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage()); // Redireciona o erro para o endpoint abaixo
            return "redirect:/clientes/auth/editar";
        } catch (ResourceNotFoundException e) {
        	 // Redireciona o erro para o endpoint abaixo
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/clientes/login";
        }
    }

    @GetMapping("/auth/alterpass")
    public String editarClientePass(Model model, Principal principal) {

    	// Recupera o cliente autenticado
        ClienteEntity cliente = clienteHelper.getClienteLogado(principal.getName());

        model.addAttribute("cliente", cliente); // Adiciona o cliente ao Model
        // Adiciona um DTO vazio ao Model; Vincula o DTO ao form
        model.addAttribute("alterarSenhaDTO", new AlterPassDTO());
        return "cliente/alterpasscliente"; 
    }

    @PostMapping("/auth/updatepass")
    public String updateClientePass(
            @ModelAttribute("alterarSenhaDTO") AlterPassDTO alterPassDTO,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        try {
        	// Recupera o cliente autenticado
            ClienteEntity clienteLogado = clienteHelper.getClienteLogado(principal.getName());

            clienteService.alterPassword( // Envia para o "clienteService" os dados abaixo
                clienteLogado.getId(),
                alterPassDTO.getSenhaAtual(),
                alterPassDTO.getNovaSenha(),
                alterPassDTO.getConfirmaSenha()
            );

            // Se der certo a etapa acima, envia uma "mensagem de sucesso" para o endpoint abaixo
            redirectAttributes.addFlashAttribute("sucesso", "Senha atualizada com sucesso!");
            return "redirect:/clientes/auth/detalhes-cliente";
        } catch (InvalidPassException | ConfirmPassNullException e) {// Se der erro na senha/ confirmação de senha
            redirectAttributes.addFlashAttribute("erro", e.getMessage()); // Envia a mensagem de erro para o endpoint abaixo
            return "redirect:/clientes/auth/alterpass";
        } catch (ResourceNotFoundException e) { // Se der erro na busca do cliente
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado."); // Envia uma mensagem para o endpoint abaixo
            return "redirect:/clientes/login";
        }
    }

}
