package br.com.wepdev.poc_catalogo.exception;

public class DatabaseException extends RuntimeException {

    public DatabaseException(String mensagem) {
        super(mensagem);
    }
}
