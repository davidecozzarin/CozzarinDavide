package it.units.expressionserver.server.components;

/**
 * Class to manage and track server statistics related to client responses.
 */
public class ServerStats {
    private double totalOkResponses;
    private double totalResponseTime;
    private double maxResponseTime;

    public ServerStats() {
        this.totalOkResponses = 0;
        this.totalResponseTime = 0;
        this.maxResponseTime = 0;
    }

    public synchronized void recordResponse(double responseTime) {
        totalOkResponses++;
        totalResponseTime += responseTime;
        if (responseTime > maxResponseTime) {
            maxResponseTime = responseTime;
        }
    }

    public synchronized double getTotalResponses() {
        return totalOkResponses;
    }

    public synchronized double getAverageResponseTime() {
        if (totalOkResponses == 0) {
            return 0;
        }
        return totalResponseTime / totalOkResponses;
    }

    public synchronized double getMaxResponseTime() {
        return maxResponseTime;
    }
}
