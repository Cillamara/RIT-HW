package game;


/**
 * A class that represents a single dot in a game of Dots and Boxes.
 * Each dot is uniquely defined by its row and column coordinate.
 *
 *
 * @author Tyler Ronnenberg
 */
public class Dot {
    /**
     * The String representation of a dot
     */
    private static final String DOT = ".";

    /**
     * Fields for row and column coordinates. Defined by constructor.
     */
    private final int row;
    private final int column;


    public Dot(int row, int column){
        //Create a dot. Store row and column coordinates for each dot
        this.row = row;
        this.column = column;
    }


    public int getRow() {
        //Returns row coordinate for a dot
        return row;
    }


    public int getColumn() {
        //Returns the column coordinate for a dot
        return column;
    }


    @Override
    public String toString() {
        //Return the string representation of a dot
        return DOT;
    }


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

