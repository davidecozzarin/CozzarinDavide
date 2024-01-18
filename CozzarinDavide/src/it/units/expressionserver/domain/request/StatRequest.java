package it.units.expressionserver.domain.request;

import it.units.expressionserver.exceptions.ProcessException;
import it.units.expressionserver.domain.response.OkResponse;
import it.units.expressionserver.domain.response.Response;
import it.units.expressionserver.server.Server;

public class StatRequest implements Request {
    private final String statRequestType;

    /**
     * Constructs a new StatRequest instance.
     *
     * @param statRequestType The type of statistic requested.
     */
    public StatRequest(String statRequestType) {
        this.statRequestType = statRequestType;
    }

    /**
     * Processes the stat request and returns the corresponding response.
     *
     * @param server    The server instance.
     * @param startTime The start time of the stat request processing.
     * @return The response to the stat request.
     * @throws ProcessException If there is an error during the processing of the stat request.
     */
    @Override
    public Response process(Server server, long startTime) throws ProcessException {
        return switch (getStatRequestType()) {
            case "STAT_REQS" -> new OkResponse(String.format("%.6f", server.getServerStats().getTotalResponses()), System.nanoTime() - startTime, server.getServerStats());
            case "STAT_AVG_TIME" -> new OkResponse(String.format("%.6f", server.getServerStats().getAverageResponseTime()), System.nanoTime() - startTime, server.getServerStats());
            case "STAT_MAX_TIME" -> new OkResponse(String.format("%.6f", server.getServerStats().getMaxResponseTime()), System.nanoTime() - startTime, server.getServerStats());
            default -> throw new ProcessException("Invalid StatRequest type");
        };
    }

    /**
     * Gets the type of stat request.
     *
     * @return The stat request type.
     */
    public String getStatRequestType(){
        return this.statRequestType;
    }
}
