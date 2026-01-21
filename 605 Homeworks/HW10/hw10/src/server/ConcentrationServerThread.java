package server;

import common.ConcentrationException;
import common.ConcentrationProtocol;
import game.ConcentrationBoard;
import game.ConcentrationCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Server protocol for a game of Concentration.
 *
 * This is part of the controller in the MVC architecture.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ConcentrationServerThread extends Thread implements ConcentrationProtocol {

    /** Socket connected to client. Set by main server class */
    private Socket socket = null;
    /** Board dimension set by main server class */
    private final int boardDimension;
    /** Thread ID set by main server class*/
    private final int ID;

    /**
     * Constructor for a new server thread.
     * Used by main server class to construct a unique game for a client.
     *
     * @param socket Server-side socket
     * @param dimension Game board dimension
     * @param ID ID of the thread
     */
    public ConcentrationServerThread(Socket socket, int dimension, int ID) {
        super("ConcentrationServerThread");
        this.socket = socket;
        this.boardDimension = dimension;
        this.ID = ID;

    }

    /**
     * Server-side protocol for a game of Concentration.
     *
     * Will only reveive a single message at a time from the client of the form:
     *      REVEAL <row> <col>
     *
     * Server constructs a ConcentrationBoard object and updates it based on
     *  client commands.
     */
    public void run() {
        System.out.println("Concentration server starting on port " + this.socket.getPort() + " DIM= " + this.boardDimension);
        System.out.println("Client #" + this.ID + ": Client " + this.ID + " connected: Socket[addr=/ " + this.socket.getInetAddress() + ",port=" + this.socket.getPort() + ",localport=" + this.socket.getLocalPort() + "]");


        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()))
        ) {
            String inputLine, outputLine;
            //Create game instance and send board dimensions to client
            ConcentrationBoard serverBoard = new ConcentrationBoard(boardDimension, true);
            System.out.println("Client #" + this.ID + ": Client started...");
            System.out.println("Client #" + this.ID + ":");
            System.out.println(serverBoard);
//            System.out.println("Waiting for client input...");
            out.println(ConcentrationProtocol.BOARD_DIM + " " + boardDimension);
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Reading input...");
                System.out.println("Client #" + this.ID + ": received:" + inputLine);
                String[] inputArray = inputLine.split(" ");

                try {
                    int row = Integer.parseInt(inputArray[1]);
                    int col = Integer.parseInt(inputArray[2]);
                    ConcentrationBoard.CardMatch matchResult = serverBoard.reveal(row, col);
                    ConcentrationCard cardToReveal = serverBoard.getCard(row, col);
                    outputLine = ConcentrationProtocol.CARD + " " + row + " " + col + " " + cardToReveal.getLetter();
                    System.out.println("Client #" + this.ID + ": sending:" + outputLine);
                    out.println(outputLine);
                    if (matchResult != null) {
                        ConcentrationCard card1 = matchResult.getCard1();
                        ConcentrationCard card2 = matchResult.getCard2();
                        if (matchResult.isMatch()) {
                            outputLine = ConcentrationProtocol.MATCH + " " + card1.getRow() + " " + card1.getCol() + " " + card2.getRow() + " " + card2.getCol();
                        } else {
                            outputLine = ConcentrationProtocol.MISMATCH + " " + card1.getRow() + " " + card1.getCol() + " " + card2.getRow() + " " + card2.getCol();
                        }
                        System.out.println("Client #" + this.ID + ": sending:" + outputLine);
                        out.println(outputLine);
                    }
                    if (serverBoard.gameOver()) {
                        System.out.println("Game over!");
                        out.println(ConcentrationProtocol.GAME_OVER);
                    }
                } catch (ConcentrationException e) {
                    System.out.println("Received bad coordinates");
                    out.println(e);
                }
            }
            socket.close();
        } catch (IOException | ConcentrationException e) {
            System.out.println(e);
        }
    }
}
