package it.units.expressionserver.server.components;

import it.units.expressionserver.domain.request.Request;
import it.units.expressionserver.domain.response.ErrorResponse;
import it.units.expressionserver.domain.response.Response;
import it.units.expressionserver.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientHandler extends Thread implements Runnable{
    private final Socket clientSocket;
    private final Server server;
    private final RequestParser requestParser;


    /**
     * Constructs a new ClientHandler instance with the given Socket and ExpressionServer.
     * Initializes a new RequestParser with the provided Server.
     *
     * @param clientSocket The Socket through which the client is connected.
     * @param server The Server instance associated with this client handler.
     */
    public ClientHandler(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.requestParser = new RequestParser();
    }

    /**
     * The main method for handling client connections and processing requests.
     * Continuously reads requests from the client, processes them, and sends the responses back to the client.
     * If the client sends a quit command or closes the connection abruptly, the connection is terminated.
     */
    @Override
    public void run() {
        try (clientSocket) {
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true);

            String line;
            Request request;
            while ((line = reader.readLine()) != null) {
                try {
                    long startTime = System.nanoTime();
                    if(line.equals(server.getQuitCommand())){
                        break;
                    }
                    request = requestParser.parseRequest(line);
                    Response response = request.process(server, startTime);
                    writer.println(response.printResponse());
                } catch (Exception e) {
                    Response errorResponse = new ErrorResponse(e.getMessage());
                    writer.println(errorResponse.printResponse());
                }
            }
            System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Client %2$s abruptly closed connection\n", System.currentTimeMillis(), clientSocket.getInetAddress());

        } catch (IOException e) {
            System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] IO error: %2$s%n", System.currentTimeMillis(), e);
        } finally {
            System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Client %2$s disconnected from Server%n", System.currentTimeMillis(), clientSocket.getInetAddress());
        }
    }

}

