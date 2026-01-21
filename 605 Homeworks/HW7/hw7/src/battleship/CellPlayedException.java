package battleship;

import java.io.Serializable;

/**
 * A BattleshipException that informs the program that it attempted to "hit"
 *  the same Cell instance more than once
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class CellPlayedException extends BattleshipException
        implements Serializable {

    /** Message do display when exception is caught*/
    static final String ALREADY_HIT = "This cell has already been hit";

    /**
     * Constructor for the CellPlayed exception.
     * Gives the coordinates of the cell that was already hit
     * @param row row of the already hit cell
     * @param column column of the already hit cell
     */
    public CellPlayedException(int row, int column) {
        super(row, column, ALREADY_HIT);
    }
}
