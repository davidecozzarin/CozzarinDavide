package it.units.expressionserver.exceptions;

public class RequestException extends IllegalArgumentException {
    public RequestException(String message) {
        super(message);
    }
    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

