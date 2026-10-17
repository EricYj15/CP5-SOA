package br.com.fiap3ess.autoescola3ess.exception.type;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String message) {
        super(message);
    }
}
