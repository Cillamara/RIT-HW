package Server2ElectricBugaloo;

import java.io.IOException;
import java.net.ServerSocket;


public class ServerC {

    private static final String USAGE_MESSAGE = "Usage: java ConcentrationServer <port> <board dimension>";


    private static void validateArguments(String[] args) {
        if (args.length != 2) {
            System.err.println(USAGE_MESSAGE);
            System.exit(1);
        }
    }

    private static void startGameServer(int port, int dimension) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server active on port " + port);
            manageClientConnections(serverSocket, dimension);
        } catch (IOException e) {
            System.err.println("Port unavailable: " + port);
            System.exit(-1);
        }
    }

    private static void manageClientConnections(ServerSocket serverSocket, int dimension) {
        int nextClientId = 1;
        while (true) {
            try {
                new ServerCThread(
                        serverSocket.accept(),
                        dimension,
                        nextClientId++
                ).start();
            } catch (IOException e) {
                System.err.println("Client connection failed");
            }
        }
    }
    public static void main(String[] args) {
        validateArguments(args);

        final int port = Integer.parseInt(args[0]);
        final int dimension = Integer.parseInt(args[1]);

        startGameServer(port, dimension);
    }
}
