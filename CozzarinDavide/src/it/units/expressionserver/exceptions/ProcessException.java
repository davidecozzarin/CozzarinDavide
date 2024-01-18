package it.units.expressionserver.exceptions;

public class ProcessException extends Exception {
    public ProcessException(String message) {
        super(message);
    }
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}