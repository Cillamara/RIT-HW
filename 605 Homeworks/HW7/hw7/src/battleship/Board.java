package battleship;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The class to represent the grid of cells (squares). A collection of ships is
 * also kept so the Board can be asked if the game is over. The class is
 * Serializable so that its instance can be saved to a file in binary form
 * using an ObjectOutputStream and restored with an ObjectInputStream. Because
 * the object holds references to all other objects in the system, no other
 *  objects need to be separately saved.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Board implements Serializable {
    int height, width;
    Cell[][] cells;
    ArrayList<Ship> ships;

    /**
     * Construct a board.
     * @param height the number of cells down
     * @param width the number of cells across
     */
    public Board(int height, int width) {
        this.height = height;
        this.width = width;
        this.ships = new ArrayList<>();
        cells = new Cell[height][width];
        for (int row = 0; row < height; row++)
            for (int column = 0; column < width; column++)
                cells[row][column] = new Cell(row, column);
    }

    /**
     * Getter for height. Used for error checking
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Getter for width. Used for error checking
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Fetch the Cell object at the given location.
     * @param row row number (0-based)
     * @param column column number (0-based)
     * @return the Cell created for this position on the board
     * @throws OutOfBoundsException if either coordinate is negative or too
     *  high
     */
    public Cell getCell(int row, int column) throws OutOfBoundsException {
        if (row < 0 || row >= height || column < 0 || column >= width)
            throw new OutOfBoundsException(row, column);
        return cells[row][column];
    }

    /**
     * Display the board in character form to the user. Cells' display
     * characters are described in Cell.
     * Output is double-spaced in both dimensions.
     * The numbers of the columns appear above the first row, and the numbers
     * of each row appears to the left of the row.
     * @param out the output stream to which the display should be sent
     */
    public void display(PrintStream out) {
        StringBuilder displayBoard = new StringBuilder();
        displayBoard.append("\n ");
        for (int i = 0; i < width; i++) {
            displayBoard.append(" ").append(i);
        }
        displayBoard.append("\n");
        for (int i = 0; i < height; i++) {
            displayBoard.append(i);
            for (int j = 0; j < width; j++) {
                displayBoard.append(" ").append(cells[i][j].displayHitStatus()
                );
            }
            displayBoard.append("\n");
        }
        out.println(displayBoard);
    }

    /**
     * This is the "cheating" form of the display because the user can see
     * where the unsunk parts of ships are. Cells' display characters are
     * described in Cell. Output is double-spaced in both dimensions. The
     * numbers of the columns appear above the first row, and the numbers of
     * each row appears to the left of the row.
     * @param out The output stream to which the display should be sent
     */
    public void fullDisplay(PrintStream out) throws OutOfBoundsException {
        StringBuilder revealBoard = new StringBuilder();
        revealBoard.append("\n ");
        for (int i = 0; i < getWidth(); i++) {
            revealBoard.append(" ").append(i);
        }
        revealBoard.append("\n");
        for (int i = 0; i < getHeight(); i++) {
            revealBoard.append(i);
            for (int j = 0; j < getWidth(); j++) {
                revealBoard.append(" ").append(getCell(i,
                        j).displayChar());
            }
            revealBoard.append("\n");
        }
        out.println(revealBoard);
    }

    /**
     * Add a ship to this board's list of ships
     * @param ship The ship to be added to the board
     */
    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    /**
     * Check if all ships have been sunk.
     * @return True if all ships' isSunk fields are true
     *          False if at least one ship's isSunk field is false
     */
    public boolean allSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk())
                return false;
        }
        return true;
    }

}
