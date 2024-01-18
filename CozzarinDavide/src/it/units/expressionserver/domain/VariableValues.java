package it.units.expressionserver.domain;

import it.units.expressionserver.exceptions.VariableValuesException;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class VariableValues {

    private final static int PRECISION = 12;
    private static final Pattern VARNAME_PATTERN = Pattern.compile("^[a-z][a-z0-9]*$");

    private final String variableName;
    private final double lower;
    private final double step;
    private final double upper;

    /**
     * Constructs a new VariableValues instance with the specified parameters.
     *
     * @param variableName The name of the variable.
     * @param lower        The lower bound of the variable's range.
     * @param step         The step size between consecutive values in the range.
     * @param upper        The upper bound of the variable's range.
     * @throws IllegalArgumentException If the variable name is invalid.
     */
    public VariableValues(String variableName, double lower, double step, double upper) {
        if (!VARNAME_PATTERN.matcher(variableName).matches()) {
            throw new IllegalArgumentException("Invalid variable name: " + variableName);
        }
        this.variableName = variableName;
        this.lower = lower;
        this.step = step;
        this.upper = upper;
    }

    /**
     * Generates a list of values within the specified range based on the lower, step, and upper bounds.
     *
     * @return A list of generated values within the specified range.
     * @throws VariableValuesException If the step is not greater than 0.
     */
    public List<Double> generateValues() throws VariableValuesException {
        List<Double> values = new ArrayList<>();
        if (step <= 0) {
            throw new VariableValuesException("Step should be greater than 0");
        }

        for (double value = lower; value <= upper; value = round(value + step)) {
            values.add(value);
        }

        return values;
    }

    private double round(double value) {
        long factor = (long) Math.pow(10, VariableValues.PRECISION);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public double getLower() {return lower;}
    public double getStep() {
        return step;
    }
    public double getUpper() {
        return upper;
    }
    public String getVariableName() {
        return variableName;
    }

}

