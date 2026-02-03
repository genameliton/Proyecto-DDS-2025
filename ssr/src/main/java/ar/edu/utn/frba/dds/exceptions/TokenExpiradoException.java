package ar.edu.utn.frba.dds.exceptions;

public class TokenExpiradoException extends RuntimeException {
    public TokenExpiradoException(String message) {
        super(message);
    }
}