package com.devsoft.rgdi_store.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.devsoft.rgdi_store.entities.ClienteEntity;
import com.devsoft.rgdi_store.entities.EnderecoEntity;
import com.devsoft.rgdi_store.entities.EnderecoTipo;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.ClienteRepository;
import com.devsoft.rgdi_store.services.ClienteService;
import com.devsoft.rgdi_store.services.exceptions.All.ConfirmPassNullException;
import com.devsoft.rgdi_store.services.exceptions.All.CpfExistsException;
import com.devsoft.rgdi_store.services.exceptions.All.EmailDivergException;
import com.devsoft.rgdi_store.services.exceptions.All.EmailExistsException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidCpfException;
import com.devsoft.rgdi_store.services.exceptions.All.InvalidPassException;
import com.devsoft.rgdi_store.services.exceptions.All.NameValidationException;
import com.devsoft.rgdi_store.validation.cliente.ClienteValidationSaveService;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

	
    private final ClienteService clienteService;
    private final ClienteRepository repository;   
    
    //injeção de dependência via construtor
    public ClienteController(ClienteService clienteService, ClienteRepository repository) {
    	this.clienteService = clienteService;
    	this.repository = repository; 
    }
    
    @GetMapping("/login")
    public String loginCliente() {
        return "login-cliente";
    }
    
    @GetMapping("/logout")
    public String logoutCliente() {
    	return "index";
    }
    
    @GetMapping("/admin")
    public String posLoginCliente() {
        return "home-cliente"; // Renderiza o template home-admin.html
    }

    @GetMapping("/cadastrar")
    public String cadastrarCliente(Model model) {
        model.addAttribute("cliente", new ClienteEntity());
        return "/cliente/cadcliente";
    }

    @PostMapping("/salvar-cliente")
    public String salvarCliente(@ModelAttribute("cliente") ClienteEntity cliente,
                                @RequestParam("confirmaSenha") String confirmaSenha,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // Valida os campos do cliente, incluindo a confirmação da senha
            ClienteValidationSaveService.validateCliente(cliente, repository, confirmaSenha);
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

        // Se houver erros de validação, retorna ao formulário
        if (result.hasErrors()) {
            model.addAttribute("cliente", cliente);
            return "cliente/cadcliente";
        }

        // Define o grupo de usuário, caso não tenha sido atribuído
        if (cliente.getGrupo() == null) {
            cliente.setGrupo(UserGroup.ROLE_CLIENT);
        }

        try {
            ClienteEntity savedCliente = clienteService.saveClienteOnly(cliente, confirmaSenha);
            redirectAttributes.addAttribute("clienteId", savedCliente.getId());
            return "redirect:/clientes/cadastrar-endereco/{clienteId}";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao salvar cliente. Tente novamente.");
            return "cliente/cadcliente";
        }
    }

 	@GetMapping("/cadastrar-endereco/{clienteId}")
    public String mostrarFormularioEnderecos(@PathVariable Long clienteId, Model model) {
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoFaturamento", new EnderecoEntity());
        model.addAttribute("enderecoEntrega", new EnderecoEntity());
        return "cliente/cadendereco";
    }

    @PostMapping("/salvar-endereco-faturamento-inicial")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> salvarEnderecoFaturamentoInicial(@RequestParam("clienteId") Long clienteId,
                                                                                  @ModelAttribute("enderecoFaturamento") EnderecoEntity enderecoFaturamento) {
        enderecoFaturamento.setTipo(EnderecoTipo.FATURAMENTO); // Garante que o tipo esteja setado
        clienteService.saveEndereco(clienteId, enderecoFaturamento);
        Map<String, Long> response = new HashMap<>();
        response.put("clienteId", clienteId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/salvar-endereco-entrega")
    public String salvarEnderecoEntrega(@RequestParam("clienteId") Long clienteId,
                                        @ModelAttribute("enderecoEntrega") EnderecoEntity enderecoEntrega) {
        enderecoEntrega.setTipo(EnderecoTipo.ENTREGA); // Garante que o tipo esteja setado
        clienteService.saveEndereco(clienteId, enderecoEntrega);
        return "redirect:/clientes/detalhes/" + clienteId;
    }    
    

    @GetMapping("/detalhes/{id}")
    public String detalhesCliente(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.findClienteById(id);
        model.addAttribute("cliente", cliente);
        return "/cliente/detalhes"; // Crie uma página de detalhes do cliente
    }

    @GetMapping("/editar/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        ClienteEntity cliente = clienteService.findClienteById(id);
        model.addAttribute("cliente", cliente);
        return "usuario/listuser"; // Adapte conforme sua necessidade
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateModal(@PathVariable Long id,
                                         @RequestBody ClienteEntity cliente,
                                         BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        ClienteEntity updated = clienteService.update(id, cliente);
        return ResponseEntity.ok(updated);
    }
}
