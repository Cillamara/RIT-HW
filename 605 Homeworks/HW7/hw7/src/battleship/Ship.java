package battleship;

import java.io.Serializable;

/**
 * A single ship in a Battleship game
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Ship implements Serializable {

    /** Message to be displayed once a ship is sunk*/
    public static final String SUNK_MESSAGE = "A battleship has been sunk!";
    /**
     * Integers representing:
     * Uppermost row and leftmost column of the ship
     * Ship length
     * Number of times the ship has been hit
     */
    int uRow, lCol, length, timesHit;
    /** Orientation of the ship*/
    Orientation ort;
    /** Boolean flag for if the ship has been sunk*/
    boolean isSunk;

    /**
     * Initialize this new ship's state. Tell the Board object and each
     * involved Cell object about the existence of this ship by trying to put
     *  the ship at each applicable Cell.
     *
     * @param board Holds a collection of ships
     * @param uRow The uppermost row the ship is on
     * @param lCol The leftmost column the ship is on
     * @param ort The ship's orientation
     * @param length The length of the ship
     * @throws OverlapException If this ship would overlap an existing ship
     * @throws OutOfBoundsException If this ship would extend beyond the board
     */
    public Ship(Board board, int uRow, int lCol, Orientation ort, int length)
            throws OverlapException, OutOfBoundsException {
        //Check start coord
        if (uRow < 0 || lCol < 0 || uRow >= board.height ||
                lCol >= board.width) {
            throw new OutOfBoundsException(uRow, lCol);
        }
        this.uRow = uRow;
        this.lCol = lCol;
        this.ort = ort;
        this.length = length;
        this.timesHit = 0;
        this.isSunk = false;
        //Add ship to the board
        board.addShip(this);
        //Add ship to the cell
        //Attempt to put horizontal ship in its cells
        if (this.ort == Orientation.HORIZONTAL) {
            //Check out of bounds
            if (this.lCol + (this.length) > board.width) {
                throw new OutOfBoundsException(this.uRow, this.lCol);
            }
            //Add ship to cell
            for (int i = this.lCol; i < (this.lCol + this.length); i++) {
                board.getCell(this.uRow, i).putShip(this);
            }

        //Attempt to put vertical ship in its cells
        } else {
            //Check out of bounds
            if (this.uRow + (this.length) > board.height) {
                throw new OutOfBoundsException(this.uRow, this.lCol);
            }
            //Place ship
            for (int i = this.uRow; i < (this.uRow + this.length); i++) {
                board.getCell(i, this.lCol).putShip(this);
            }
        }
    }

    /**
     * Method for hitting a ship. Called on by the cell being hit.
     * Adds 1 to the timesHit field of the ship, then checks if timesHit is
     * equal to length. If it is, the ship is considered sunk,
     *  and a message is displayed.
     */
    public void hit() {
        timesHit++;
        if (timesHit == length) {
            this.isSunk = true;
            System.out.println(SUNK_MESSAGE);
        }
    }

    /**
     * A method for checking if a ship has been sunk.
     *  Used by the Board object to determine when the game ends
     *
     * @return Boolean value of this ship's isSunk field
     */
    public boolean isSunk() {
        return this.isSunk;
    }
}
