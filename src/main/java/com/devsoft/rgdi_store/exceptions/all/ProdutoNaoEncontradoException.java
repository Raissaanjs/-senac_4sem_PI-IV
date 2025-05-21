package com.devsoft.rgdi_store.exceptions.all;

public class ProdutoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public ProdutoNaoEncontradoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

