package com.devsoft.rgdi_store.services.exceptions;

//"RuntimeException" não exige a nível de compilação inserir try/ Catch
public class DatabaseException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	//Exige que insira uma mensagem
	public DatabaseException(String msg) {
		super(msg);
	}

}
