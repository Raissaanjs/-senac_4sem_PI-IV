package com.devsoft.rgdi_store.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsoft.rgdi_store.dto.UserDto;
import com.devsoft.rgdi_store.dto.ValidationGroups;
import com.devsoft.rgdi_store.services.UserService;
import com.devsoft.rgdi_store.services.exceptions.FieldValidationException;

import jakarta.validation.groups.Default;

@RestController
@CrossOrigin("*") //libera acesso de conexões do frontend
@RequestMapping(value = "/usuarios")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//lista com paginação
	@GetMapping
	public ResponseEntity<Page<UserDto>> findAll( Pageable pageable){
		Page<UserDto> dto = userService.findAll(pageable);
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping(value = "/{id}")
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
	@PutMapping(value = "/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Validated({ ValidationGroups.Update.class, Default.class }) @RequestBody UserDto dto) {
		validatePass(dto);
		dto = userService.update(id, dto); // Salvei e peguei a referência
        return ResponseEntity.ok(dto);
    }
	
	//alterna status
	@PutMapping(value = "/{id}/status")
	public ResponseEntity<UserDto> changeStatus(@PathVariable Long id) {
	    UserDto dto = userService.changeStatus(id);
	    return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		userService.delete(id);
		return ResponseEntity.noContent().build();
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
