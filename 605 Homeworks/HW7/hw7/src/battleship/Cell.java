package battleship;

import java.io.Serializable;

/**
 * A single spot on the Battleship game board.
 * A cell knows if there is a ship on it, and it remembers
 * if it has been hit.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Cell implements Serializable {

    /** Character to display for a ship that has been entirely sunk */
    public static final char SUNK_SHIP_SECTION = '*';

    /** Character to display for a ship that has been hit but not sunk */
    public static final char HIT_SHIP_SECTION = '☐';

    /** Character to display for a water cell that has been hit */
    public static final char HIT_WATER = '.';

    /**
     * Character to display for a water cell that has not been hit.
     * This character is also used for an un-hit ship segment.
     */
    public static final char PRISTINE_WATER = '_';

    /**
     * Character to display for a ship section that has not been
     * sunk, when revealing the hidden locations of ships
     */
    public static final char HIDDEN_SHIP_SECTION = 'S';

    /** Row and column of the cell*/
    int row, column;
    /** Boolean flags for if the cell has been hit and if the cell has a ship*/
    boolean isHit, hasShip;
    /** The ship associated with this cell. Null until assigned*/
    Ship ship;

    /**
     * Create a new cell.
     * @param row Row of the cell
     * @param column Column of the cell
     */
    public Cell(int row, int column) {
        this.row = row;
        this.column = column;
        this.ship = null;
        this.hasShip = false;
    }

    /**
     * Attempt to associate a ship with the cell.
     * Ships typically cover more than one cell, so the same ship will usually
     * be passed to more than one Cell's putShip method.
     * @param shipToAdd The ship object to associate with the cell
     * @throws OverlapException If a ship already exists in the cell
     */
    public void putShip(Ship shipToAdd) throws OverlapException {
        if (hasShip) {
            throw new OverlapException(this.row, this.column);
        }
        this.hasShip = true;
        this.ship = shipToAdd;
    }

    /**
     * Simulate hitting this cell. If there is a ship here, it will be hit.
     * Calling this method changes the status of the cell, as reflected by
     * displayChar() and displayHitStatus()
     * @throws CellPlayedException If the cell has already been hit
     */
    public void hit() throws CellPlayedException {
        if (!this.isHit) {
            if (hasShip) {
                this.ship.hit();
            }
            this.isHit = true;
        } else {
            throw new CellPlayedException(this.row, this.column);
        }
    }

    /**
     * Return a character representing the state of this Cell but without
     * revealing unhit portions of ships.
     * Unhit portions of ships appear as PRISTINE_WATER.
     * @return one of the characters declared as a constant static field in
     *          this class, according to the state of the cell and
     *          the state of the ship upon it, if any.
     */
    public char displayHitStatus() {
        if (isHit) {
            if (hasShip) {
                if (ship.isSunk()) {
                    return SUNK_SHIP_SECTION;
                } else {
                    return HIT_SHIP_SECTION;
                }
            } else {
                return HIT_WATER;
            }
        }
        return PRISTINE_WATER;
    }

    /**
     * Return a character representing the state of this Cell.
     * This display method reveals all.
     * @return one of the characters declared as a constant static field in
     *          this class, according to the state of the cell and"
     *          the state of the ship upon it, if any.
     */
    public char displayChar() {
        if (isHit) {
            if (hasShip) {
                if (ship.isSunk()) {
                    return SUNK_SHIP_SECTION;
                } else {
                    return HIT_SHIP_SECTION;
                }
            } else {
                return HIT_WATER;
            }
        } else {
            if (hasShip) {
                return HIDDEN_SHIP_SECTION;
            } else {
                return PRISTINE_WATER;
            }
        }
    }
}
