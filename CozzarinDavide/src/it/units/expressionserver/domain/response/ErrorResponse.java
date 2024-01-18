package it.units.expressionserver.domain.response;

public class ErrorResponse implements Response {
    private final String errorMessage;

    /**
     * Constructs a new ErrorResponse instance with the specified error message.
     *
     * @param errorMessage The error message describing the encountered error.
     */
    public ErrorResponse(String errorMessage){
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the string representation of the ErrorResponse, formatted as "ERR: errorMessage".
     *
     * @return The formatted string representing the ErrorResponse.
     */
    @Override
    public String printResponse(){
        return "ERR: " + errorMessage;
    }

}
