package it.units.expressionserver.server;

import it.units.expressionserver.server.components.ClientHandler;
import it.units.expressionserver.server.components.ServerStats;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService executorService;
    private final ServerStats serverStats;
    private static final String QUIT_COMMAND = "BYE";

    /**
     * Constructor for the Server. It initializes the server port,
     * creates the ExecutorService for handling connections and computations,
     * and instantiates the ServerStats for collecting statistics.
     */
    public Server(int port) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.serverStats = new ServerStats();
    }

    /**
     * The main method for the Server class. It parses the command line arguments for the server port number,
     * instantiates the Server, and runs it.
     *
     * @param args Command line arguments, expecting the server port number as the first argument.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar CozzarinDavide.jar <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Unable to start the Server: " + e.getMessage());
        }
    }


    /**
     * Starts the server, allowing it to accept client connections. Each connection is handled in a separate thread.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Server started, listening on port %2$d%n", System.currentTimeMillis(), port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] New connection from client: %2$s\n\n", System.currentTimeMillis(), clientSocket.getRemoteSocketAddress());
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    executorService.execute(clientHandler);
                } catch (IOException e) {
                    System.err.printf("Error accepting client connection due to %s\n", e);
                }
            }
        }catch (IOException e){
            System.err.printf("Error starting the server on port %1$s due to %2$s", port, e.getMessage());
        }
    }

    /**
     * Getter for the QUIT_COMMAND String, it signifies that a client wishes to disconnect
     *
     * @return The QUIT_COMMAND String.
     */
    public String getQuitCommand() {
        return QUIT_COMMAND;
    }

    /**
     * Getter for the serverStats.
     *
     * @return A ServerStats instance that collects statistics on the server's operations.
     */
    public ServerStats getServerStats(){
        return serverStats;
    }
}


