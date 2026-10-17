package br.com.fiap3ess.autoescola3ess.exception.type;

public class AlunoNotFoundException extends RuntimeException {
    public AlunoNotFoundException(String message) {
        super(message);
    }
}