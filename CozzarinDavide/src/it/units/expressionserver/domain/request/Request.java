package it.units.expressionserver.domain.request;

import it.units.expressionserver.exceptions.ProcessException;
import it.units.expressionserver.domain.response.Response;
import it.units.expressionserver.server.Server;

/**
 * Represents a generic request that can be processed by the server.
 */
public interface Request {

    /**
     * Processes the request and returns the corresponding response.
     *
     * @param server    The server instance.
     * @param startTime The start time of the request processing.
     * @return The response to the request.
     * @throws ProcessException If there is an error during the processing of the request.
     */
    Response process(Server server, long startTime) throws ProcessException;
}
