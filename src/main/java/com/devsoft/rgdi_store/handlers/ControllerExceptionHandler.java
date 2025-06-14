package com.devsoft.rgdi_store.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsoft.rgdi_store.exceptions.DataIntegrityViolationException;
import com.devsoft.rgdi_store.exceptions.DatabaseException;
import com.devsoft.rgdi_store.exceptions.FieldValidationException;
import com.devsoft.rgdi_store.exceptions.ResourceNotFoundException;
import com.devsoft.rgdi_store.exceptions.all.ConfirmPassNullException;
import com.devsoft.rgdi_store.exceptions.all.CpfExistsException;
import com.devsoft.rgdi_store.exceptions.all.EmailDivergException;
import com.devsoft.rgdi_store.exceptions.all.EmailExistsException;
import com.devsoft.rgdi_store.exceptions.all.InvalidCpfException;
import com.devsoft.rgdi_store.exceptions.all.InvalidPassException;
import com.devsoft.rgdi_store.exceptions.all.NameValidationException;
import com.devsoft.rgdi_store.exceptions.all.ProdutoNaoEncontradoException;
import com.devsoft.rgdi_store.validation.base.CustomError;
import com.devsoft.rgdi_store.validation.base.ValidationError;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	//"@ExceptionHandler" fará a interceptação da exceção
	//"HttpServletRequest" possibilita pegar a url que deu a exceção
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(status.value(), e.getMessage());
		return ResponseEntity.status(status).body(err);
	}
	
	//trata exceção de DB
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<CustomError> Database(DatabaseException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(status.value(), e.getMessage());
		return ResponseEntity.status(status).body(err);
	}
	
	//Trata excessão de nome
	@ExceptionHandler(NameValidationException.class)
	public ResponseEntity<CustomError> NameValidation(NameValidationException ex, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;

	    CustomError error = new CustomError(
	        status.value(),
	        ex.getMessage()
	    );

	    return ResponseEntity.status(status).body(error);
	}
	
	//Trata excessão de CPF
	@ExceptionHandler(InvalidCpfException.class)
	public ResponseEntity<CustomError> handleInvalidCpf(InvalidCpfException ex, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;

	    CustomError error = new CustomError(
	        status.value(),
	        ex.getMessage()
	    );

	    return ResponseEntity.status(status).body(error);
	}	
	
	//Trata excessão de cpf duplicado
	@ExceptionHandler(CpfExistsException.class)
	public ResponseEntity<CustomError> CpfExists(CpfExistsException e, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;

	    // Cria o erro customizado com informações gerais
	    CustomError err = new CustomError(
	        status.value(),
	        e.getMessage()
	    );	    

	    return ResponseEntity.status(status).body(err);
	}
	
	//trata excessão de campo email vazio/ fora do formato
	@ExceptionHandler(EmailDivergException.class)
	public ResponseEntity<CustomError> EmailDiverg(EmailDivergException e, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;

	    // Cria o erro customizado com informações gerais
	    CustomError err = new CustomError(
	        status.value(),
	        e.getMessage()
	    );

	    return ResponseEntity.status(status).body(err);
	}
	
	//Trata excessão de email duplicado
	@ExceptionHandler(EmailExistsException.class)
	public ResponseEntity<CustomError> handleEmailAlreadyExists(EmailExistsException e, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;

	    // Cria o erro customizado com informações gerais
	    CustomError err = new CustomError(
	        status.value(),
	        e.getMessage()
	    );

	    return ResponseEntity.status(status).body(err);
	}
		
	//Trata excessão de senha vazia ou com menos de 6 dígitos
	@ExceptionHandler(InvalidPassException.class)
    public ResponseEntity<CustomError> InvalidPassword(InvalidPassException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Cria o erro customizado
        CustomError error = new CustomError(
            status.value(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(error);
    }	
	
	//Trata excessão de confirmação de senha
	@ExceptionHandler(ConfirmPassNullException.class)
    public ResponseEntity<CustomError> ConfirmPassNull(ConfirmPassNullException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Cria o erro customizado
        CustomError error = new CustomError(
            status.value(),
            ex.getMessage()
        );

        return ResponseEntity.status(status).body(error);
    }
	
	//Trata excessão de Produto não encontrado
	@ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<String> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<CustomError> DataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		//Gera um novo objeto com os dados da exceção
		CustomError err = new CustomError(
				status.value(),
				e.getMessage()
		);
		return ResponseEntity.status(status).body(err);
	}
		
		
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomError> methodArgumentNotValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		//fazendo Upcasting - fazer um objeto se passar por um objeto que seja um supertipo dele
		ValidationError err = new ValidationError(
				status.value(), 
				//"Dados inválidos");
				e.getMessage()
		);
		
		//Irá percorrer todos os erros que estiver na lista abaixo
		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			//adiciona a mensagem que definimos no "UserDto-> Bean Validation"
			err.addError(
					f.getField(),
					f.getDefaultMessage()
			);
		}
		return ResponseEntity.status(status).body(err);
	}
	
	
	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<CustomError> fieldValidationException(FieldValidationException e, HttpServletRequest request) {
	    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;        
	    ValidationError error = new ValidationError(
	    		status.value(),
	    		//"Dados inválidos");
				e.getMessage()
		);

	    for (FieldError f : e.getFieldErrors()) {
	        error.addError(
	        		f.getField(),
	        		f.getDefaultMessage()
	        );
	    }

	    return ResponseEntity.status(status).body(error);
	}

}
