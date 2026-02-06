package br.com.wepdev.poc_catalogo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

@ControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(CategoriaNaoEncontradaException.class)
    public ResponseEntity<StandardError> categoriaNaoEncontrada(CategoriaNaoEncontradaException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now());
        err.setStatus(status.value());
        err.setError("Categoria não encontrada");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }


    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> erroDeViolacaoIntegridade(DatabaseException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now());
        err.setStatus(status.value());
        err.setError("Database exception");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<StandardError> produtoNaoEncontrado(ProdutoNaoEncontradoException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError();
        err.setTimestamp(OffsetDateTime.now());
        err.setStatus(status.value());
        err.setError("Produto não encontrado");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(err);
    }


}
