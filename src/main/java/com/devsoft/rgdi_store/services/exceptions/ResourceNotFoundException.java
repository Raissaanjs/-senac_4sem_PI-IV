package com.devsoft.rgdi_store.services.exceptions;

//"RuntimeException" não exige a nível de compilação inserir try/ Catch
public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	//Exige que insira uma mensagem
	public ResourceNotFoundException(String msg) {
		super(msg);
	}
}