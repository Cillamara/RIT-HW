package game;

import java.util.ArrayList;

/**
 * The main class for representing and playing a game of Dots and Boxes.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class GameBoard {

    /**
     * fields for lines, dots, and player
     */
    public Lines lines;
    private Dot[][] dots;
    public static Player player;
    /**
     * fields for num of moves, player specific number of moves, the number of
     * boxes player blue has claimed, and the number of boxes player red
     * has claimed
     */
    public static int numOfMoves = 0;
    public static int playerNum = 0;
    public static int redScore = 0;
    public static int blueScore = 0;

    /**
     * Construct a game board
     * @param rows Number of rows in the game board
     * @param columns Number of columns in the game board
     */
    public GameBoard(int rows, int columns) {

        //Create dots, lines, and boxes for the game
        dots = new Dot[rows+1][columns+1];
        Box[][] boxes = new Box[rows+1][columns+1];
        lines = new Lines(rows, columns, dots);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                boxes[row][column] = new Box(row, column, lines);
            }
        }
        //Set player to Red
        player = Player.RED;
    }

    /**
     * Determine if the game is over. Game ends when number of lines equals
     * the number of moves made
     * @return True if number of moves equals number of lines.
     */
    public boolean gameOver() {
        return numOfMoves == Lines.numOfLines;
    }

    /**
     * Determine which player is active
     * @return The active player
     */
    public Player whoseTurn() {
        if (playerNum % 2 == 0)
            player = Player.RED;
        else
            player = Player.BLUE;
        return player;
    }

    /**
     * Checks if a set of coordinates corresponds to a valid, unclaimed line.
     * @param row1 Row of the first dot of the line
     * @param column1 Column of the first dot of the line
     * @param row2 Row of the second dot of the line
     * @param column2 Column of the second dot of the line
     * @return True if line is valid, false otherwise
     */
    public boolean isLineValid(int row1, int column1, int row2, int column2) {
        return (((row1 + 1 == row2) && (column1 == column2)) ||
                ((row1 == row2) && (column1 + 1 == column2))) &&
                !lines.getLine(row1, column1, row2, column2).hasOwner() &&
                (row1 < dots.length && row2 <dots.length &&
                column1 < dots[0].length && column2 <dots[0].length);
    }

    /**
     * Make a move in the game given a valid line to claim. A move is made by
     * specifying an unclaimed line to be owned by the current player. If the
     * move claims a box that player gets to go again, otherwise the next turn
     * is swapped to the other player.
     * @param row1 Row of the first dot of the line
     * @param column1 Column of the first dot of the line
     * @param row2 Row of the second dot of the line
     * @param column2 Column of the second dot of the line
     */
    public void makeMove(int row1, int column1, int row2, int column2) {
        //Claim a line
        lines.getLine(row1, column1, row2, column2).claim(player);
        //Check if at least one box is claimed
        ArrayList<Box> afterMoveBoxes =
                lines.getLine(row1, column1, row2, column2).getBoxes();
        boolean alreadyIncremented = false;
            for (int i = 0; i < afterMoveBoxes.size(); i++) {
                Box afterMoveBox = afterMoveBoxes.get(i);
                if (afterMoveBox.getOwner().equals(player)) {
                    if (player == Player.RED)
                        redScore++;
                    if (player == Player.BLUE)
                        blueScore++;
                    if (!alreadyIncremented){
                        playerNum++;
                        alreadyIncremented = true;
                    }
                }
            }
            playerNum++;
            numOfMoves++;
    }
    /**
     * Builds and returns a string representation of the board
     * @return a string representation of the board
     */
    @Override
    public String toString() {
        int maxRows = dots.length;
        int maxCols = dots[0].length;
        StringBuilder board = new StringBuilder();
        for(int r = 0; r < maxRows - 1; r++) {
            for(int c = 0; c < maxCols - 1; c++) {
                board.append(".");
                if (!lines.getLine(r, c, r, c + 1)
                        .getOwner().getLabel().equals(" "))
                    board.append("-");
                else
                    board.append(" ");
            }
            board.append(".\n");
            for(int c = 0; c < maxCols - 1; c++) {
                if (!lines.getLine(r, c, r+1, c)
                        .getOwner().getLabel().equals(" "))
                    board.append("|");
                else
                    board.append(" ");
                board.append(lines.getLine(r, c+1, r+1, c+1)
                        .getBoxes().getFirst().getOwner().getLabel());
            }
            if (!lines.getLine(r, maxCols-1, r+1, maxCols - 1)
                    .getOwner().getLabel().equals(" "))
                board.append("|");
            else
                board.append(" ");
            board.append("\n");
        }
        for(int c = 0; c < maxCols - 1; c++) {
            board.append(".");
            if (!lines.getLine(maxRows-1, c, maxRows-1, c+1)
                    .getOwner().getLabel().equals(" "))
                board.append("-");
            else
                board.append(" ");
        }
        board.append(".\n");
        return board.toString();
    }
}
