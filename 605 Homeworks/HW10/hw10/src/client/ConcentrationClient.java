package client;

import common.ConcentrationException;
import common.ConcentrationProtocol;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client for a game of Concentration. The client has only one command:
 *
 *      REVEAL row col
 *
 * This queries the server for a card and reveals the letter at that coordinate
 *
 * This class is part of the controller in the MVC architecture.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ConcentrationClient implements ConcentrationProtocol {

    /**
     * Client-side protocol for a game of Concentration.
     * @param args Initial arguments should be of the form:
     *             java ConcentrationClient <host name> <port number>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java ConcentrationClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String fromUser;
            String fromServer;

            //Get board dimensions from server thread
            String initialResponse = in.readLine();
            int boardDim = Integer.parseInt(initialResponse.split(" ")[1]);
            //Build board from initial server message
            ConcentrationClientBoard clientBoard = new ConcentrationClientBoard(boardDim);
            System.out.print(clientBoard);
            System.out.print("> ");

            //Output server messages. Read user input.
            while ((fromUser = stdIn.readLine()) != null) {
                try {
                    String[] outputCheck = fromUser.split(" ");
                    //Check for correct input
                    if (outputCheck.length != 3 || !outputCheck[0].equalsIgnoreCase("REVEAL")
                            || (Integer.parseInt(outputCheck[1]) < 0 || Integer.parseInt(outputCheck[1]) >= boardDim
                            || Integer.parseInt(outputCheck[2]) < 0 || Integer.parseInt(outputCheck[2]) >= boardDim)) {
                        throw new ConcentrationException(ConcentrationProtocol.ERROR + ": \"REVEAL [0 <= row < "+ boardDim + "]  [0 <= col < " + boardDim + "]\"");
                    }
                    //Check if card already revealed
                    if (clientBoard.getCard(Integer.parseInt(outputCheck[1]), Integer.parseInt(outputCheck[2])) != '.') {
                        throw new ConcentrationException(ConcentrationProtocol.ERROR + ": Card already revealed");
                    }
                    //If valid, send command to server
                    out.println(fromUser);
                    //Read server response
                    fromServer = in.readLine();
                    //Format server message for processing
                    String[] incomingCheck = fromServer.split(" ");
                    //If server responds with a CARD_MSG, reveal card
                    if (incomingCheck.length == 4 && incomingCheck[0].equals(ConcentrationProtocol.CARD)) {
                        int row = Integer.parseInt(incomingCheck[1]);
                        int col = Integer.parseInt(incomingCheck[2]);
                        char letter = Character.toUpperCase(incomingCheck[3].charAt(0));
                        clientBoard.reveal(row, col, letter);
                        System.out.print(clientBoard);
                        //If this is the second card revealed, listen for a second message
                        if (clientBoard.getListenForSecond()) {
                            fromServer = in.readLine();
                            incomingCheck = fromServer.split(" ");
                            //If match is made, update board, otherwise repeat loop
                            if (incomingCheck.length == 5 &&
                                    (incomingCheck[0].equals(ConcentrationProtocol.MATCH)) || incomingCheck[0].equals(ConcentrationProtocol.MISMATCH)) {
                                int row1 = Integer.parseInt(incomingCheck[1]);
                                int col1 = Integer.parseInt(incomingCheck[2]);
                                int row2 = Integer.parseInt(incomingCheck[3]);
                                int col2 = Integer.parseInt(incomingCheck[4]);

                                //If mismatch, hide revealed cards and reprint board
                                if (incomingCheck[0].equals(ConcentrationProtocol.MISMATCH)) {
                                    clientBoard.hide(row1, col1, row2, col2);
                                    System.out.print(clientBoard);
                                }
                            }
                            //If game over message received. Only checks after a match is made.
                            if (clientBoard.gameOver()) {
                                fromServer = in.readLine();
                                if (fromServer.equals(ConcentrationProtocol.GAME_OVER)) {
                                    System.out.println("You won!");
                                    break;
                                }
                            }
                        }
                    }

                } catch (ConcentrationException ce) {
                    System.out.println(ce.getMessage());
                }
                System.out.print("> ");
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (ConcentrationException e) {
            System.err.println(e.getMessage());
        }
    }
}
