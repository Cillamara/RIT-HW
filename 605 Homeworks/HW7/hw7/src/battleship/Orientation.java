package battleship;

/**
 * The orientation of a ship.
 * The names of the enum values were chosen because they
 * are descriptive and match the words put in the configuration
 * files.
 *
 * @author  CS RIT
 */
public enum Orientation {
    HORIZONTAL(0, 1), VERTICAL(1, 0);

    /**
     * Use it to loop through all of the cell locations a ship
     * is on, based on the upper left end of the ship.
     */
    public int rDelta, cDelta;

    /**
     * Associate a direction vector with the orientation.
     *
     * @param rDelta how much the row number changes in this direction
     * @param cDelta how much the column number changes
     *               in this direction
     */
    Orientation(int rDelta, int cDelta) {
        this.rDelta = rDelta;
        this.cDelta = cDelta;
    }
}