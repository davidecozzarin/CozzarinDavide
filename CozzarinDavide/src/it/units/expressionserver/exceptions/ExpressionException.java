package it.units.expressionserver.exceptions;

public class ExpressionException extends IllegalArgumentException{
    public ExpressionException(String message) {
        super(message);
    }
    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
    }


}
