package br.com.fiap3ess.autoescola3ess.exception;

import br.com.fiap3ess.autoescola3ess.exception.type.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarGenericNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>> tratarBadRequest(MethodArgumentNotValidException e) {
        List<FieldError> erros = e.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosBadRequest::new).toList());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErro> tratarCorpoInvalido() {
        return ResponseEntity.badRequest().body(new DadosErro("Corpo da requisição ausente ou em formato inválido!"));
    }

    @ExceptionHandler({
            InstrutorNotFoundException.class,
            AlunoNotFoundException.class,
            UsuarioNotFoundException.class,
            InstrucaoNotFoundException.class,
            CepNotFoundException.class
    })
    public ResponseEntity<DadosErro> tratarNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(e.getMessage()));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErro> tratarRegraDeNegocio(ValidacaoException e) {
        return ResponseEntity.badRequest().body(new DadosErro(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DadosErro> tratarConflito() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new DadosErro("Registro duplicado: já existe um cadastro com estes dados únicos!"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DadosErro> tratarFalhaAutenticacao() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new DadosErro("Credenciais inválidas!"));
    }

    @ExceptionHandler(ServicoExternoException.class)
    public ResponseEntity<DadosErro> tratarServicoExterno(ServicoExternoException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new DadosErro(e.getMessage()));
    }

    private record DadosBadRequest(String campo, String mensagem) {
        public DadosBadRequest(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    private record DadosErro(String mensagem) {
    }
}
