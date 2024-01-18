package it.units.expressionserver.exceptions;

public class VariableValuesException extends IllegalArgumentException {
    public VariableValuesException(String message) {
        super(message);
    }
    public VariableValuesException(String message, Throwable cause) {
        super(message, cause);
    }
}
