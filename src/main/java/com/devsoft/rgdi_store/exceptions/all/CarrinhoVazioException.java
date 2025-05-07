package com.devsoft.rgdi_store.exceptions.all;

public class CarrinhoVazioException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;

	public CarrinhoVazioException(String mensagem) {
        super(mensagem);
    }
}

