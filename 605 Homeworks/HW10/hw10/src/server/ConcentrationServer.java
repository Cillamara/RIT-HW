package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Server for a game of concentration.
 * Listens for client connection request.
 * If connection succeeds, opens a new server thread for that client.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ConcentrationServer {

    /**
     * Main server method for establishing a connection with multiple clients.
     * Each client connection will start a new thread with a unique game.
     *
     * @param args Port number to listen to and dimension of game board
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ConcentrationServer <port number> <board dimension>");
            System.exit(1);
        } else {
            int port = Integer.parseInt(args[0]);
            int dimension = Integer.parseInt(args[1]);
            boolean listening = true;
            int ID = 1;

            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (listening) {
                    new ConcentrationServerThread(serverSocket.accept(), dimension, ID).start();
                    ID++;
                }
            } catch(IOException e) {
                System.err.println("Could not listen on port: " + port);
                System.exit(-1);
            }

        }
    }
}
