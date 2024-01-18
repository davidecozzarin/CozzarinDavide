package it.units.expressionserver.domain.request;

import it.units.expressionserver.domain.Expression;
import it.units.expressionserver.domain.VariableValues;
import it.units.expressionserver.exceptions.InvalidComputationKindException;
import it.units.expressionserver.exceptions.ProcessException;
import it.units.expressionserver.exceptions.VariableValuesException;
import it.units.expressionserver.domain.response.OkResponse;
import it.units.expressionserver.domain.response.Response;
import it.units.expressionserver.server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ComputationRequest implements Request {
    private final String computationKind;
    private final List<VariableValues> variableValuesList;
    private final List<Expression> expressions;

    /**
     * Constructs a new ComputationRequest instance.
     *
     * @param computationKind    The kind of computation to be performed.
     * @param variableValuesList The list of variable values.
     * @param expressions        The list of expressions to be evaluated.
     */
    public ComputationRequest(String computationKind, List<VariableValues> variableValuesList, List<Expression> expressions) {
        this.computationKind = computationKind;
        this.variableValuesList = variableValuesList;
        this.expressions = expressions;
    }

    public String getComputationKind() {
        return this.computationKind;
    }

    public List<VariableValues> getVariableValuesList() {
        return this.variableValuesList;
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    /**
     * Processes the computation request and returns the corresponding response.
     *
     * @param server    The server instance.
     * @param startTime The start time of the computation process.
     * @return The response to the computation request.
     * @throws ProcessException If there is an error during the processing of the computation request.
     */
    @Override
    public Response process(Server server, long startTime) throws ProcessException {

        HashMap<String, List<Double>> variableValuesMap = createVariableValueMappings(this.getVariableValuesList());
        List<List<Double>> variableValuesList = new ArrayList<>(variableValuesMap.values());
        List<List<Double>> tuples = switch (this.getComputationKind().split("_")[1]) {
            case "GRID" -> new ArrayList<>(getCartesianProduct(variableValuesList));
            case "LIST" -> new ArrayList<>(getElementWiseMerge(variableValuesList));
            default -> throw new InvalidComputationKindException("Invalid computation kind");
        };

        double computationResult;
        if (this.getComputationKind().startsWith("COUNT")) {
            computationResult = tuples.size();
        } else {
            List<Expression> expressions = this.getExpressions();
            List<Double> results = new ArrayList<>();
            for (List<Double> tuple : tuples) {
                for (Expression expression : expressions) {
                    HashMap<String, Double> variableValuesForExpression = new HashMap<>();
                    int i = 0;
                    for (String variableName : variableValuesMap.keySet()) {
                        variableValuesForExpression.put(variableName, tuple.get(i));
                        i++;
                    }
                    results.add(expression.evaluate(variableValuesForExpression));
                }
            }

            computationResult = switch (this.getComputationKind().split("_")[0]) {
                case "MIN" -> results.stream().mapToDouble(v -> v).min().orElseThrow(() -> new ProcessException("Failed to compute min"));
                case "MAX" -> results.stream().mapToDouble(v -> v).max().orElseThrow(() -> new ProcessException("Failed to compute max"));
                case "AVG" -> results.stream().mapToDouble(v -> v).average().orElseThrow(() -> new ProcessException("Failed to compute avg"));
                default -> throw new InvalidComputationKindException("Invalid computation kind");
            };
        }

        String formattedComputationResult = String.format(Locale.US, "%.6f", computationResult);

        return new OkResponse(formattedComputationResult, System.nanoTime() - startTime, server.getServerStats());
    }

    /**
     * Creates a mapping between variables and their values.
     *
     * @param variableValuesList The list of variable values.
     * @return A map containing variable names as keys and lists of corresponding values.
     * @throws VariableValuesException If there is an error in generating variable values.
     */
    public HashMap<String, List<Double>> createVariableValueMappings(List<VariableValues> variableValuesList) throws VariableValuesException {
        HashMap<String, List<Double>> variableValuesMap = new HashMap<>();

        for (VariableValues variableValues : variableValuesList) {
            try {
                List<Double> values = variableValues.generateValues();
                variableValuesMap.put(variableValues.getVariableName(), values);
            } catch (VariableValuesException e) {
                throw new VariableValuesException("Error generating variable values for " + variableValues.getVariableName(), e);
            }
        }

        return variableValuesMap;
    }

    /**
     * Generates the Cartesian product of the input lists of numbers.
     *
     * @param lists The input lists of numbers.
     * @return The Cartesian product as a list of lists of numbers.
     */
    private List<List<Double>> getCartesianProduct(List<List<Double>> lists) {
        List<List<Double>> resultLists = new ArrayList<>();
        cartesianProduct(0, new ArrayList<>(), lists, resultLists);
        return resultLists;
    }

    /**
     * Support function for generating the Cartesian product recursively.
     *
     * @param index       The current index in the lists.
     * @param current     The current list of numbers.
     * @param lists       The input lists of numbers.
     * @param resultLists The resulting list of lists of numbers.
     */
    private void cartesianProduct(int index, List<Double> current, List<List<Double>> lists, List<List<Double>> resultLists) {
        if (index == lists.size()) {
            resultLists.add(new ArrayList<>(current));
            return;
        }

        for (Double num : lists.get(index)) {
            current.add(num);
            cartesianProduct(index + 1, current, lists, resultLists);
            current.remove(current.size() - 1);
        }
    }

    /**
     * Combines elements from multiple lists into a list of tuples.
     *
     * @param lists The input lists of numbers.
     * @return The merged list of tuples.
     * @throws IllegalArgumentException If variable ranges have different lengths for element-wise merging.
     */
    private List<List<Double>> getElementWiseMerge(List<List<Double>> lists) {
        if (!lists.stream().allMatch(list -> list.size() == lists.get(0).size())) {
            throw new IllegalArgumentException("All non-empty variable ranges must have the same length for element-wise merging. Check the length of the lists.");
        }

        int numRows = lists.size();
        int numCols = lists.get(0).size();
        List<List<Double>> result = new ArrayList<>(numCols);

        for (int i = 0; i < numCols; i++) {
            List<Double> mergedColumn = new ArrayList<>(numRows);

            for (List<Double> currentRow : lists) {
                Double value = currentRow.get(i);

                if (value == null) {
                    value = 0.0;
                }

                mergedColumn.add(value);
            }

            result.add(mergedColumn);
        }

        return result;
    }
}
