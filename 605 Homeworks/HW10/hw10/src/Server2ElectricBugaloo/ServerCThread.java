package Server2ElectricBugaloo;
import common.ConcentrationException;
import common.ConcentrationProtocol;
import game.ConcentrationBoard;
import game.ConcentrationCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerCThread extends Thread implements ConcentrationProtocol {

    private static final String CLIENT_PREFIX = "Client #%d: ";

    private final Socket clientSocket;
    private final int boardDimension;
    private final int clientId;
    private ConcentrationBoard gameBoard;

    /**
     * Creates a new game thread for a client connection.
     *
     * @param socket Client connection socket
     * @param dimension Game board dimension
     * @param clientId Unique client identifier
     */
    public ServerCThread(Socket socket, int dimension, int clientId) {
        super("ConcentrationServerThread-" + clientId);
        this.clientSocket = socket;
        this.boardDimension = dimension;
        this.clientId = clientId;
    }

    /**
     * Main game loop handling client communication and game logic.
     */
    @Override
    public void run() {
        logConnectionDetails();

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            initializeGame(out);
            processClientRequests(in, out);

        } catch (IOException | ConcentrationException e) {
            logError("Communication error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void logConnectionDetails() {
        System.out.printf(CLIENT_PREFIX + "Connected - Remote: %s:%d%n",
                clientId, clientSocket.getInetAddress(), clientSocket.getPort());
    }

    private void initializeGame(PrintWriter out) throws ConcentrationException {
        this.gameBoard = new ConcentrationBoard(boardDimension, true);
        out.println(BOARD_DIM + " " + boardDimension);
        logGameState("Game initialized");
    }

    private void processClientRequests(BufferedReader in, PrintWriter out) throws IOException {
        String clientInput;
        while ((clientInput = in.readLine()) != null) {
            logMessage("Received: " + clientInput);
            try {
                processClientCommand(clientInput, out);
            } catch (ConcentrationException e) {
                out.println(e.getMessage());
                logError("Invalid move: " + e.getMessage());
            }

            if (gameBoard.gameOver()) {
                out.println(GAME_OVER);
                logMessage("Game Over");
                break;
            }
        }
    }

    private void processClientCommand(String command, PrintWriter out) throws ConcentrationException {
        String[] parts = command.split(" ");
        if (parts.length != 3 || !REVEAL.equals(parts[0])) {
            throw new ConcentrationException("Invalid command format");
        }

        int row = parseCoordinate(parts[1], "row");
        int col = parseCoordinate(parts[2], "column");

        ConcentrationBoard.CardMatch match = gameBoard.reveal(row, col);
        sendCardReveal(out, row, col);

        if (match != null) {
            sendMatchResult(out, match);
        }
    }

    private int parseCoordinate(String input, String coordinateName) throws ConcentrationException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ConcentrationException("Invalid " + coordinateName + " value");
        }
    }

    private void sendCardReveal(PrintWriter out, int row, int col) throws ConcentrationException {
        ConcentrationCard card = gameBoard.getCard(row, col);
        String message = String.format("%s %d %d %c", CARD, row, col, card.getLetter());
        sendToClient(out, message);
    }

    private void sendMatchResult(PrintWriter out, ConcentrationBoard.CardMatch match) {
        ConcentrationCard c1 = match.getCard1();
        ConcentrationCard c2 = match.getCard2();
        String resultType = match.isMatch() ? MATCH : MISMATCH;

        String message = String.format("%s %d %d %d %d",
                resultType, c1.getRow(), c1.getCol(), c2.getRow(), c2.getCol());
        sendToClient(out, message);
    }

    private void sendToClient(PrintWriter out, String message) {
        out.println(message);
        logMessage("Sent: " + message);
    }

    private void logGameState(String message) {
        System.out.printf(CLIENT_PREFIX + "%s%n%s%n", clientId, message, gameBoard);
    }

    private void logMessage(String message) {
        System.out.printf(CLIENT_PREFIX + "%s%n", clientId, message);
    }

    private void logError(String error) {
        System.err.printf(CLIENT_PREFIX + "%s%n", clientId, error);
    }

    private void closeConnection() {
        try {
            clientSocket.close();
            logMessage("Connection closed");
        } catch (IOException e) {
            logError("Error closing connection: " + e.getMessage());
        }
    }
}