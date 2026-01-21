package game;

import java.util.Scanner;

/**
 * The main program for the pen and paper game, Dots And Boxes.
 * https://en.wikipedia.org/wiki/Dots_and_Boxes
 * The program is run on the command line as:
 * $ java game.DotsAndBoxes rows columns
 *
 * When prompted, the player to play enters two coordinates
 * to draw a line between two dots.
 *
 * The coordinates must go left to right for a horizontal line,
 * and top to bottom for a vertical line.
 *
 * The user may prematurely end the game by entering a 'q'
 * instead of coordinates to quit the game
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class DotsAndBoxes {
    /**
     * Fields for the prompt for the user to enter coordinates
     */
    static final String PROMPT = "> ";
    private GameBoard gameBoard;

    public DotsAndBoxes(int rows, int columns) {
        gameBoard = new GameBoard(rows, columns);
    }

    /**
     * Main gameplay loop
     */
    public void play(){
        System.out.println("Welcome to Dots and Boxes!");
        //Build Game Board
        Scanner scanner = new Scanner(System.in);

        //Enter the game loop
        while (!gameBoard.gameOver()){
            //Display game board after each move
            System.out.println(gameBoard.toString());
            //Display player color
            System.out.println("It is " + gameBoard.whoseTurn() + "'s turn");
            System.out.println("Enter line coordinates: ");
            System.out.print(PROMPT);
            //If 'q' is entered at any point, quit program
            String row1 = scanner.next();
            if (row1.equals("q")){
                System.out.println("Leaving game...");
                break;
            }
            String column1 = scanner.next();
            if (column1.equals("q")){
                System.out.println("Leaving game...");
                break;
            }
            String row2 = scanner.next();
            if (row2.equals("q")){
                System.out.println("Leaving game...");
                break;
            }
            String column2 = scanner.next();
            if (column2.equals("q")){
                System.out.println("Leaving game...");
                break;
            }
            int r1 = Integer.parseInt(row1);
            int c1 = Integer.parseInt(column1);
            int r2 = Integer.parseInt(row2);
            int c2 = Integer.parseInt(column2);
            if (!gameBoard.isLineValid(r1, c1, r2, c2)){
                System.out.println("Invalid line!");
            } else{
                gameBoard.makeMove(r1, c1, r2, c2);
            }
        }
        System.out.println(gameBoard.toString());
        if (GameBoard.redScore > GameBoard.blueScore){
            System.out.println("RED Wins " + GameBoard.redScore +
                    " to " + GameBoard.blueScore);
        }
        if (GameBoard.blueScore > GameBoard.redScore){
            System.out.println("BLUE Wins " + GameBoard.blueScore +
                    " to " + GameBoard.redScore);
        }
        if (GameBoard.redScore == GameBoard.blueScore){
            System.out.println("TIE game " + GameBoard.redScore +
                    " to " + GameBoard.blueScore);
        }
    }

    /**
     * The main routine gets the row and column from the command line and then
     * instantiates and plays the game.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        DotsAndBoxes dotsAndBoxes = new DotsAndBoxes(2, 2);
        dotsAndBoxes.play();
    }



}
