package ar.edu.utn.frba.dds.exceptions;

public class ContrasenaIncorrectaException extends RuntimeException {
    public ContrasenaIncorrectaException() {
        super("La contraseña no corresponde al email ingresado.");
    }
}