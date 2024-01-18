package it.units.expressionserver.domain;

import java.util.Map;

import it.units.expressionserver.domain.nodes.Constant;
import it.units.expressionserver.domain.nodes.Node;
import it.units.expressionserver.domain.nodes.Operator;
import it.units.expressionserver.domain.nodes.Variable;


public class Expression {
    private final Node root;

    /**
     * Constructs a new Expression instance with the specified root node.
     *
     * @param root The root node of the expression tree.
     */
    public Expression(Node root) {
        this.root = root;
    }

    /**
     * Evaluates the expression based on the provided variable values.
     *
     * @param variableValues A map containing variable names and their corresponding values.
     * @return The result of evaluating the expression.
     * @throws IllegalArgumentException If an unvalued variable is encountered during evaluation.
     */
    public double evaluate(Map<String, Double> variableValues) {
        return evaluate(root, variableValues);
    }
    private double evaluate(Node node, Map<String, Double> variableValues) {
        if (node instanceof Constant) {
            return ((Constant) node).getValue();
        } else if (node instanceof Variable) {
            String variableName = ((Variable) node).getName();
            if (!variableValues.containsKey(variableName)) {
                throw new IllegalArgumentException("(ComputationException) Unvalued variable " + variableName);
            }
            return variableValues.get(variableName);
        } else if (node instanceof Operator operator) {
            double[] childValues = new double[operator.getChildren().size()];
            for (int i = 0; i < childValues.length; i++) {
                childValues[i] = evaluate(operator.getChildren().get(i), variableValues);
            }
            return operator.getType().getFunction().apply(childValues);
        } else {
            throw new IllegalArgumentException("Unknown Node type: " + node.getClass());
        }
    }

}

