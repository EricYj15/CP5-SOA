package br.com.fiap3ess.autoescola3ess.exception.type;

public class ServicoExternoException extends RuntimeException {
    public ServicoExternoException(String message) {
        super(message);
    }

    public ServicoExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
