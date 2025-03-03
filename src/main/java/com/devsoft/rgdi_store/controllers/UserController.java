package com.devsoft.rgdi_store.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.services.UserService;
import com.devsoft.rgdi_store.services.exceptions.FieldValidationException;
import com.devsoft.rgdi_store.validation.ValidationGroups;

import jakarta.validation.groups.Default;

@Controller
@RequestMapping(value = "/usuarios")
public class UserController {
	
	@Autowired
	private PagedResourcesAssembler<UserDto> pagedResourcesAssembler;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("")
    public String redirectToAuth() {
        return "redirect:/auth"; // Redireciona para /auth
    }
	
	@GetMapping("/cadastrar")
	public String cadastrar(Model model) {
	    model.addAttribute("dto", new UserDto());
	    return "/usuario/cadastro";
	}
	
	@GetMapping("/listar")
	public String listar(Model model, Pageable pageable) {
	    Page<UserDto> dtoPage = userService.findAll(pageable);
	    model.addAttribute("usuarios", dtoPage.getContent()); // Adiciona a lista de usuários
	    model.addAttribute("page", dtoPage); // Adiciona os metadados da página
	    return "usuario/lista"; // Caminho do arquivo HTML em templates/usuario/listar.html
	}

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute("dto") UserDto dto, BindingResult bindingResult, Model model) {
	    // Verifica se há erros de validação
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("dto", dto); // Mantém os dados preenchidos no formulário
	        return "usuario/cadastro"; // Retorna para a página do formulário
	    }

	    // Valida a senha antes de salvar
	    validatePass(dto);

	    // Chama o mesmo serviço usado no método insert
	    userService.insert(dto);

	    // Redireciona para a página de listagem após salvar
	    return "redirect:/usuarios/listar";
	}

	
	//lista com paginação
	@GetMapping("/api")
	public ResponseEntity<PagedModel<EntityModel<UserDto>>> findAll(Pageable pageable) {
	    Page<UserDto> dtoPage = userService.findAll(pageable);
	    PagedModel<EntityModel<UserDto>> pagedModel = pagedResourcesAssembler.toModel(dtoPage);
	    return ResponseEntity.ok(pagedModel);
	}

	
	@GetMapping(value = "/detalhes/{id}")
	public ResponseEntity<UserDto> findById(@PathVariable Long id) {
		UserDto dto = userService.findById(id);
		return ResponseEntity.ok(dto);
	}
	
	//não esquecer o "@Valid" - necessario para validacao de campos
	@PostMapping
    public ResponseEntity<UserDto> insert(@Validated({ ValidationGroups.Create.class, Default.class }) @RequestBody UserDto dto) {		
		validatePass(dto);
		dto = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
	
	//não esquecer o "@Validated" - necessario para validacao de campos
	//@Validated (Criado grupo de validação definido o email não ser usado no "update")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @Validated({ ValidationGroups.Update.class, Default.class }) @RequestBody UserDto dto, BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = bindingResult.getFieldErrors()
	            .stream()
	            .collect(Collectors.toMap(
	                FieldError::getField,
	                FieldError::getDefaultMessage
	            ));
	        return ResponseEntity.badRequest().body(errors);
	    }

	    validatePass(dto);
	    dto = userService.update(id, dto);
	    return ResponseEntity.ok(dto);
	}
	
	//alterna status
	@PutMapping(value = "/{id}/status")
	public ResponseEntity<UserDto> changeStatus(@PathVariable Long id) {
	    UserDto dto = userService.changeStatus(id);
	    return ResponseEntity.ok(dto);
	}	
	
	//Valida a senha
	private void validatePass(UserDto dto) {
        List<FieldError> fieldErrors = new ArrayList<>();
        
        if (!dto.getSenha().equals(dto.getConfirmasenha())) {
            fieldErrors.add(new FieldError("User", "senha/ confirmasenha", "As senhas não coincidem"));
        }

        if (!fieldErrors.isEmpty()) {
            throw new FieldValidationException("Erro de validação", fieldErrors);
        }
	}
}
