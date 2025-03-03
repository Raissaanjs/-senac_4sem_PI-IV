package com.devsoft.rgdi_store.controllers.handlers;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsoft.rgdi_store.services.exceptions.DataIntegrityViolationException;
import com.devsoft.rgdi_store.services.exceptions.DatabaseException;
import com.devsoft.rgdi_store.services.exceptions.FieldValidationException;
import com.devsoft.rgdi_store.services.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.validation.CustomError;
import com.devsoft.rgdi_store.validation.ValidationError;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	//"@ExceptionHandler" fará a interceptação da exceção
	//"HttpServletRequest" possibilita pegar a url que deu a exceção
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	//trata exceção de DB
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<CustomError> Database(DatabaseException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	//trata exceção de Unique
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<CustomError> DataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(Instant.now(), status.value(), e.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
		
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomError> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		//fazendo Upcasting - fazer um objeto se passar por um objeto que seja um supertipo dele
		ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());
		
		//Irá percorrer todos os erros que estiver na lista abaixo
		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			//adiciona a mensagem que definimos no "UserDto-> Bean Validation"
			err.addError(f.getField(), f.getDefaultMessage());
		}
		return ResponseEntity.status(status).body(err);
	}
	
	//Usado para validar senha
	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<CustomError> fieldValidationException(FieldValidationException e, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;        
	    ValidationError err = new ValidationError(Instant.now(), status.value(), "Dados inválidos", request.getRequestURI());

	    for (FieldError f : e.getFieldErrors()) {
	        err.addError(f.getField(), f.getDefaultMessage());
	    }

	    return ResponseEntity.status(status).body(err);
	}


}
