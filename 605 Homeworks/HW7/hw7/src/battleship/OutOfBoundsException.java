package battleship;

import java.io.Serializable;

/**
 * A BattleshipException that informs the program that it attempted to place a
 *  ship or hit a cell outside the bounds of the board.
 */
public class OutOfBoundsException extends BattleshipException
        implements Serializable {

    /** Message to display when exception is caught*/
    public static final String PAST_EDGE =
            "Coordinates are past the board edge";

    /**
     * Constructor for the OutOfBounds exception.
     * Gives the coordinates of a cell which is outside the bounds of the
     *  board.
     *
     * @param row Row where the error occurred
     * @param column Column where the error occurred
     */
    public OutOfBoundsException(int row, int column) {
        super(row, column, PAST_EDGE);
    }
}
