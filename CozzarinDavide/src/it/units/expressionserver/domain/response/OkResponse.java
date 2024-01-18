package it.units.expressionserver.domain.response;

import it.units.expressionserver.server.components.ServerStats;

import java.util.Locale;

public class OkResponse implements Response {
    private final String result;
    private final String responseTime;

    /**
     * Constructs a new OkResponse instance with the specified result, response time,
     * and updates server statistics with the truncated response time.
     *
     * @param result        The computation result.
     * @param responseTime  The response time in seconds.
     * @param serverStats   The server statistics instance to record the response time.
     */
    public OkResponse(String result, long responseTime, ServerStats serverStats){
        double  responseTimeInSeconds = responseTime / 1e9;
        String formattedResponseTime = String.format(Locale.US, "%.3f", responseTimeInSeconds);
        double truncatedResponseTimeInDouble = Double.parseDouble(formattedResponseTime);
        serverStats.recordResponse(truncatedResponseTimeInDouble);
        this.result = result;
        this.responseTime = formattedResponseTime;
    }

    /**
     * Returns the string representation of the OkResponse, formatted as "OK;responseTime;result".
     *
     * @return The formatted string representing the OkResponse.
     */
    @Override
    public String printResponse(){
        return "OK;" + responseTime + ";" + result;
    }

}
