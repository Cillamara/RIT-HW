package game;

/**
 * A class that represents a single dot in a game of Dots and Boxes.
 * Each dot is uniquely defined by its row and column coordinate.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Dot {
    /**
     * The String representation of a dot
     */
    public static final String DOT = ".";

    /**
     * Fields for row and column coordinates. Defined by constructor.
     */
    private final int rowCoord;
    private final int colCoord;

    /**
     * Construct an instance of a dot at given coordinates
     * @param row Row coordinate of the dot
     * @param column Column coordinate of the dot
     */
    public Dot(int row, int column){
        //Create a dot. Store row and column coordinates for each dot
        rowCoord = row;
        colCoord = column;
        if (row < 0 || column < 0)
            throw new AssertionError();
    }

    /**
     *Get the row.
     * @return the row coordinate
     */
    public int getRow() {
        //Returns row coordinate for a dot
        return rowCoord;
    }

    /**
     * Get the column.
     * @return the column coordinate
     */
    public int getColumn() {
        //Returns the column coordinate for a dot
        return colCoord;
    }

    /**
     * Return the string representation of a dot.
     * @return a dot
     */
    @Override
    public String toString() {
        //Return the string representation of a dot
        return DOT;
    }

    /**
     * Two dots are equal if they have the same row and column.
     * @param other the dot to compare with
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object other) {
        //Checks if two dots have same coordinates
        if (!(other instanceof Dot))
            return false;

        Dot otherDot = (Dot) (other);

        return this.getRow() == otherDot.getRow() &&
                this.getColumn() == otherDot.getColumn();
    }
}
