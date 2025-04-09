package com.devsoft.rgdi_store.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.devsoft.rgdi_store.dto.ClienteDto;
import com.devsoft.rgdi_store.dto.EnderecoDto;
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
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ClienteRepository repository;
	
	
	@GetMapping("/login")
	public String login(Model model) {
		 return "redirect:/login-cliente";
	}
	
	@GetMapping("/cadastrar")
    public String cadastrar(Model model) {
        // Cria um novo DTO de Cliente para o formulário
        ClienteDto dto = new ClienteDto();
        model.addAttribute("dto", dto);  // Adiciona o DTO de Cliente ao modelo
        return "cliente/cadcliente";  // Retorna o nome da página do formulário
    }
	
	//Salvar
	@PostMapping("/salvar")
	public String save(@ModelAttribute("dto") ClienteDto dto, BindingResult result, Model model) {
	    // Adiciona validações personalizadas
	    try {
	        ClienteValidationSaveService.validateCliente(dto, repository);
	    } catch (NameValidationException e) {
	        result.rejectValue("nome", "error.nome", e.getMessage());
	    } catch (InvalidCpfException | CpfExistsException e) {
	        result.rejectValue("cpf", "error.cpf", e.getMessage());
	    } catch (EmailDivergException | EmailExistsException e) {
	        result.rejectValue("email", "error.email", e.getMessage());
	    } catch (InvalidPassException | ConfirmPassNullException e) {
	        result.rejectValue("senha", "error.senha", e.getMessage());
	    }
	    
	    // Verifica se há erros de validação. Se houver manda para o Front
	    if (result.hasErrors()) {
	        model.addAttribute("dto", dto); // Mantém os dados preenchidos no formulário
	        return "/cliente/cadcliente"; // Retorna para a página do formulário
	    }

	    // Define grupo padrão caso não tenha sido selecionado
	    if (dto.getGrupo() == null) {
	        dto.setGrupo(UserGroup.ROLE_USER);
	    }	
	    
	    // Salva o cliente
        ClienteDto savedDto = clienteService.insert(dto);

        // Adiciona o ID do cliente salvo ao modelo para uso posterior
        model.addAttribute("clienteId", savedDto.getId());
        model.addAttribute("enderecoDto", new EnderecoDto());

        // Retorna para a página de cadastro com a opção de adicionar endereços
        return "cliente/cadcliente";
	}	
	
	
	@PostMapping("/adicionarEndereco")
    public String adicionarEndereco(@RequestParam Long clienteId, @ModelAttribute EnderecoDto enderecoDto, BindingResult result, Model model) {
        // Adiciona validações personalizadas para endereços, se necessário

        // Verifica se há erros de validação. Se houver, retorna para o formulário
        if (result.hasErrors()) {
            model.addAttribute("clienteId", clienteId); // Mantém o ID do cliente
            model.addAttribute("enderecoDto", enderecoDto); // Mantém os dados preenchidos no formulário
            return "cliente/cadcliente"; // Retorna para a página do formulário
        }

        // Salva o endereço para o cliente
        clienteService.salvarEndereco(clienteId, enderecoDto);

        // Retorna para a página de cadastro com a opção de adicionar mais endereços
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("enderecoDto", new EnderecoDto());
        return "cliente/cadcliente";
    }
	
	@GetMapping("/editar/{id}")
	public String editUser(@PathVariable Long id, Model model) {
	    ClienteDto dto = clienteService.findById(id); // Busca o usuário para edição
	    UserGroup[] grupos = UserGroup.values(); // Obtém todos os valores do enum UserGroup

	    model.addAttribute("dto", dto); // Adiciona o cliente ao modelo
	    model.addAttribute("grupos", grupos); // Adiciona a lista de grupos ao modelo
	    return "usuario/listuser"; // Retorna o template
	}	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateModal(
	        @PathVariable Long id,
	        @RequestBody ClienteDto dto,
	        BindingResult result) {
	    
	    // Verifica se há erros de validação
	    if (result.hasErrors()) {
	        Map<String, String> errors = result.getFieldErrors()
	            .stream()
	            .collect(Collectors.toMap(
	                FieldError::getField,
	                FieldError::getDefaultMessage
	            ));
	        return ResponseEntity.badRequest().body(errors);
	    }
	    
	    // Atualiza o usuário com os novos dados
	    dto = clienteService.update(id, dto);//retorna o update personalizado

	    return ResponseEntity.ok(dto);
	}
	
}
