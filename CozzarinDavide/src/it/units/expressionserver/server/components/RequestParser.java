package it.units.expressionserver.server.components;

import it.units.expressionserver.exceptions.ExpressionParsingException;
import it.units.expressionserver.domain.nodes.Node;
import it.units.expressionserver.domain.nodes.Parser;
import it.units.expressionserver.domain.Expression;
import it.units.expressionserver.domain.VariableValues;
import it.units.expressionserver.exceptions.VariableValuesException;
import it.units.expressionserver.exceptions.VariableValuesParsingException;
import it.units.expressionserver.exceptions.RequestParsingException;
import it.units.expressionserver.domain.request.ComputationRequest;
import it.units.expressionserver.domain.request.Request;
import it.units.expressionserver.domain.request.StatRequest;

import java.util.ArrayList;
import java.util.List;


public final class RequestParser {

    /**
     * Parses the raw request and constructs a corresponding Request object.
     *
     * @param rawRequest The raw request string.
     * @return The parsed Request object.
     * @throws RequestParsingException If there is an error in parsing the request.
     */
    public Request parseRequest(String rawRequest) throws RequestParsingException {
        rawRequest = rawRequest.trim();

        if (rawRequest.equals("STAT_REQS") || rawRequest.equals("STAT_AVG_TIME") || rawRequest.equals("STAT_MAX_TIME")) {
            return new StatRequest(rawRequest);
        }

        if (rawRequest.matches("(MIN|MAX|AVG|COUNT)_(GRID|LIST);.+;.+")) {
            String[] components = rawRequest.split(";",3);
            if (components.length < 3) {
                throw new IllegalArgumentException("Invalid ComputationRequest format");
            }
            String computationType = components[0];
            List<VariableValues> variableValues = parseVariableValues(components[1]);
            List<Expression> expressions = parseExpressions(components[2]);

            return new ComputationRequest(computationType, variableValues, expressions);

        }
        throw new RequestParsingException("Invalid request format");
    }

    /**
     * Parses the variable values string and constructs a list of VariableValues objects.
     *
     * @param variableValuesString The variable values string.
     * @return The list of VariableValues objects.
     * @throws VariableValuesParsingException If there is an error in parsing variable values.
     */
    private List<VariableValues> parseVariableValues(String variableValuesString) throws VariableValuesException {
        String[] components = variableValuesString.split(",");
        List<VariableValues> variableValuesList = new ArrayList<>();

        for (String component : components) {
            String[] subComponents = component.split(":");
            if (subComponents.length != 4) {
                throw new VariableValuesParsingException("VariableValues should be of the form VarName:JavaNum:JavaNum:JavaNum");
            }

            String variableName = subComponents[0];
            if (!variableName.matches("[a-z][a-z0-9]*")) {
                throw new IllegalArgumentException("Invalid VarName in VariableValues");
            }

            double lower, step, upper;
            try {
                lower = Double.parseDouble(subComponents[1]);
                step = Double.parseDouble(subComponents[2]);
                upper = Double.parseDouble(subComponents[3]);
            } catch (VariableValuesException e) {
                throw new VariableValuesParsingException("Invalid number format in VariableValues");
            }

            variableValuesList.add(new VariableValues(variableName, lower, step, upper));
        }
        return variableValuesList;
    }

    /**
     * Parses the expressions string and constructs a list of Expression objects.
     *
     * @param expressionsString The expressions string.
     * @return The list of Expression objects.
     * @throws ExpressionParsingException If there is an error in parsing expressions.
     */
    private List<Expression> parseExpressions(String expressionsString) throws ExpressionParsingException {
        String[] components = expressionsString.split(";");
        List<Expression> expressionsList = new ArrayList<>();

        for (String component : components) {
            Parser parser = new Parser(component);
            try {
                Node root = parser.parse();
                expressionsList.add(new Expression(root));
            } catch (Exception e) {
                throw new ExpressionParsingException("Failed to parse expression: " + component, e);
            }
        }
        return expressionsList;
    }

}




