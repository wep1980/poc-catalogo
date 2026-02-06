package br.com.wepdev.poc_catalogo.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {

    public CategoriaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
