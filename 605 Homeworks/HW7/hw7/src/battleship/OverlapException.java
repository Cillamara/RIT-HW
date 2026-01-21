package battleship;

/**
 * A BattleshipException that informs the program that it attempted to place a
 *  ship where there is already another ship.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class OverlapException extends BattleshipException {

    /** Message to be displayed when the exception is caught*/
    public static final String OVERLAP =
            "Ships placed in overlapping positions";

    /**
     * Constructor for the Overlap exception. Displays the coordinates of the
     *  Cell where the overlap occurred.
     * @param row Row of the cell
     * @param column Column of the cell
     */
    public OverlapException(int row, int column) {
        super(row, column, OVERLAP);
    }
}
