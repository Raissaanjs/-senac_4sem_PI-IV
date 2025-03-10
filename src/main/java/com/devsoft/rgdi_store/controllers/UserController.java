package com.devsoft.rgdi_store.controllers;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.security.core.Authentication;


import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.entities.UserGroup;
import com.devsoft.rgdi_store.repositories.UserRepository;
import com.devsoft.rgdi_store.services.UserService;
import com.devsoft.rgdi_store.services.exceptions.user.ConfirmPassNullException;
import com.devsoft.rgdi_store.services.exceptions.user.EmailDivergException;
import com.devsoft.rgdi_store.services.exceptions.user.EmailExistsException;
import com.devsoft.rgdi_store.services.exceptions.user.InvalidCpfException;
import com.devsoft.rgdi_store.services.exceptions.user.InvalidPassException;
import com.devsoft.rgdi_store.services.exceptions.user.NameValidationException;
import com.devsoft.rgdi_store.validation.user.UserValidationSaveService;
import com.devsoft.rgdi_store.validation.user.ValidationGroups;

import jakarta.validation.groups.Default;

@Controller
@RequestMapping("/usuarios")
public class UserController {
	
	@Autowired
	private PagedResourcesAssembler<UserDto> pagedResourcesAssembler;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository repository;
	
	@GetMapping("/cadastrar")
	public String register(Model model) {
	    model.addAttribute("dto", new UserDto());
	    return "/usuario/cadastro";
	}
	
	
	//validação para email único
	@GetMapping("/verificar-email")
	@ResponseBody
	public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
	    boolean existe = userService.existsByEmail(email);
	    return ResponseEntity.ok(existe);
	}
	
	//busca o usuário logado. Usado para não permitir o usuário logado alterar seu grupo 
	@GetMapping("/logged")
	public String getUsuarios(
	        Model model,
	        @AuthenticationPrincipal UserDetails loggedInUser,
	        Pageable pageable) {
	    if (loggedInUser instanceof UserDto) {
	        UserDto user = (UserDto) loggedInUser;
	        model.addAttribute("loggedInUser", user);
	    } else {
	        throw new IllegalStateException("Usuário logado não é do tipo esperado!");
	    }
	    model.addAttribute("usuarios", userService.findAll(pageable));
	    return "usuarios";
	}

	//Para caixa busca por nome
	@GetMapping("/buscar-nome")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public String buscarPorNomeOuTodos(
	    @RequestParam(value = "nome", required = false, defaultValue = "") String nome,
	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable,
	    Model model
	) {
	    // Busca por nome ou retorna todos os usuários com paginação
	    Page<UserDto> usuarios = (nome == null || nome.trim().isEmpty()) 
	                                ? userService.findAll(pageable) 
	                                : userService.findByName(nome, pageable);

	    // Adiciona os dados ao modelo
	    model.addAttribute("usuarios", usuarios.getContent());
	    model.addAttribute("page", usuarios);
	    model.addAttribute("nome", nome); // Para preservar o termo de busca no formulário

	    return "usuario/lista";
	}

	//Lista geral - Menu  
	@GetMapping("/listar")
	public String list(
	    Model model,
	    @PageableDefault(page = 0, size = 5, sort = "id") Pageable pageable
	) {
	    Page<UserDto> dtoPage = userService.findAll(pageable);

	    // Adiciona os resultados da página ao modelo do Thymeleaf
	    model.addAttribute("usuarios", dtoPage.getContent());
	    model.addAttribute("page", dtoPage); // Metadados da página (como total de páginas e número atual)

	    return "usuario/lista"; // Template Thymeleaf
	}

	//lista com paginação via Postman
	@GetMapping("/api")
	public ResponseEntity<PagedModel<EntityModel<UserDto>>> findAll(Pageable pageable) {
	    Page<UserDto> dtoPage = userService.findAll(pageable);
	    PagedModel<EntityModel<UserDto>> pagedModel = pagedResourcesAssembler.toModel(dtoPage);
	    return ResponseEntity.ok(pagedModel);
	}
	
	//para framework de Front
	@GetMapping("/detalhes/{id}")
	public ResponseEntity<UserDto> findById(@PathVariable Long id) {
		UserDto dto = userService.findById(id);
		return ResponseEntity.ok(dto);
	}	

			
	

	@PostMapping("/salvar")
	public String save(@ModelAttribute("dto") UserDto dto, BindingResult result, Model model) {
	    // Adiciona validações personalizadas
	    try {
	        UserValidationSaveService.validateUser(dto, repository);
	    } catch (NameValidationException e) {
	        result.rejectValue("nome", "error.nome", e.getMessage());
	    } catch (InvalidCpfException e) {
	        result.rejectValue("cpf", "error.cpf", e.getMessage());
	    } catch (EmailDivergException | EmailExistsException e) {
	        result.rejectValue("email", "error.email", e.getMessage());
	    } catch (InvalidPassException | ConfirmPassNullException e) {
	        result.rejectValue("senha", "error.senha", e.getMessage());
	    }

	    // Verifica se há erros de validação. Se houver manda para o Front
	    if (result.hasErrors()) {
	        model.addAttribute("dto", dto); // Mantém os dados preenchidos no formulário
	        return "usuario/cadastro"; // Retorna para a página do formulário
	    }

	    // Define grupo padrão caso não tenha sido selecionado
	    if (dto.getGrupo() == null) {
	        dto.setGrupo(UserGroup.ROLE_USER);
	    }

	    // Chama o método insert para salvar o usuário
	    userService.insert(dto);

	    // Redireciona para a página de listagem após salvar
	    return "redirect:/usuarios/listar";
	}

	
	//não esquecer o "@Valid" - necessario para validacao de campos
	@PostMapping(produces = "application/json;charset=UTF-8")
    public ResponseEntity<UserDto> insert(@Validated({ ValidationGroups.Create.class, Default.class }) @RequestBody UserDto dto) {	
		dto = userService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
	
	
	@GetMapping("/editar/{id}")
	public String editUser(@PathVariable Long id, Model model) {
	    UserDto dto = userService.findById(id); // Busca o usuário para edição
	    UserGroup[] grupos = UserGroup.values(); // Obtém todos os valores do enum UserGroup

	    model.addAttribute("dto", dto); // Adiciona o usuário ao modelo
	    model.addAttribute("grupos", grupos); // Adiciona a lista de grupos ao modelo
	    return "usuario/lista"; // Retorna o template
	}	
	
	//exclusivo para o MODAL/edit
	@PutMapping("/modal-update/{id}")
	public ResponseEntity<?> updateModal(
	        @PathVariable Long id,
	        @RequestBody UserDto dto,
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
	    dto = userService.updateModal(id, dto);//retorna o update personalizado

	    return ResponseEntity.ok(dto);
	}
	
	//Update geral
	//não esquecer o "@Validated" - necessario para validacao de campos
	//@Validated (Criado grupo de validação definido o email não ser usado no "update")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, 
	                                @Validated({ ValidationGroups.Update.class, Default.class }) 
	                                @RequestBody UserDto dto, 
	                                BindingResult bindingResult) {
	    if (bindingResult.hasErrors()) {
	        Map<String, String> errors = bindingResult.getFieldErrors()
	            .stream()
	            .collect(Collectors.toMap(
	                FieldError::getField,
	                FieldError::getDefaultMessage
	            ));
	        return ResponseEntity.badRequest().body(errors);
	    }	   

	    // Atualiza o usuário com os novos dados
	    dto = userService.update(id, dto);
	    return ResponseEntity.ok(dto);
	}
	
	
	//alterna status
	@PutMapping("/{id}/status")
	public ResponseEntity<UserDto> changeStatus(@PathVariable Long id) {
	    UserDto dto = userService.changeStatus(id);
	    return ResponseEntity.ok(dto);
	}	
	
}
