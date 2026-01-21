package game;

import java.util.ArrayList;

/**
 * A class representing a single line in a game of Dots and Boxes
 * A valid line is defined by two coordinates that are exactly one unit apart,
 * either horizontally or vertically.
 * A line must be defined left to right, or top to bottom
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */

public class Line {

    /**
     * String representations of different lines
     */
    public static final String EMPTY = " ";
    public static final String HORI_LINE = "-";
    public static final String VERT_LINE = "|";

    private Player player;

    /**
     * First and second dots. Defined by constructor
     */
    private final Dot first;
    private final Dot second;
    private ArrayList<Box> boxes = new ArrayList<>();

    /**
     * Creates a line
     * @param first First dot
     * @param second Second dot
     */
    public Line(Dot first, Dot second) {
        //Create a line object with two dots and store their coordinates
        //Dots must be orthogonal to be valid
        this.first = first;
        this.second = second;
        player = Player.NONE;
        if (first.equals(second) ||
                first.getRow() > second.getRow() ||
                first.getColumn() > second.getColumn()) {
            throw new AssertionError();
        }
    }

    /**
     * Get the first dot of a line
     * @return First dot of the line
     */
    public Dot getFirst(){
        //Return the first dot's coordinates
        return first;
    }

    /**
     * Get the second dot of a line
     * @return Second dot of the line
     */
    public Dot getSecond(){
        //Return the second dot's coordinates
        return second;
    }

    /**
     * Get the owner of a line
     * @return The owner of the line
     */
    public Player getOwner(){
        return player;
    }

    /**
     * Get the associated boxes of a line
     * @return An ArrayList containing the boxes associated with the line
     */
    public ArrayList<Box> getBoxes(){
        return boxes;
    }

    /**
     * Checks if a line has been claimed
     * @return True if line has been claimed, false otherwise
     */
    public boolean hasOwner(){
        return player != Player.NONE;
    }

    /**
     * Method to claim a line
     * @param owner Player whose turn it is
     */
    public void claim(Player owner){
        this.player = owner;
        for (Box box : this.boxes) {
            box.claim(owner);
        }
    }

    /**
     * Associates a line with its boxes
     * @param box Box to be associated with the line
     */
    public void setBox(Box box){
        boxes.add(box);
    }

    //Helper method to print line coordinates
//    public String lineDebug(){
//        int r1 = first.getRow();
//        int c1 = first.getColumn();
//        int r2 = second.getRow();
//        int c2 = second.getColumn();
//        return "Line between ( " + r1 + " , " + c1 + " ) and " +
//                "( " + r2 + " , " + c2 + " )";
//    }

    /**
     * Displays the string representation of a line
     * @return A space if no owner, "|" if vertical, "-" if horizontal
     */
    @Override
    public String toString(){
        if (first.getRow() == second.getRow() && !player.equals(Player.NONE)) {
            return HORI_LINE;
        } else if (first.getColumn() == second.getColumn() &&
                !player.equals(Player.NONE)) {
            return VERT_LINE;
        } else {
            return EMPTY;
        }
    }

    /**
     * Two lines are equal if they have the same dot objects
     * @param other Line object to compare to
     * @return whether the lines are equal or not
     */
    @Override
    public boolean equals(Object other){
        //Checks if two lines share the same dots
        if (!(other instanceof Line))
            return false;

        Line otherLine = (Line) (other);

        return this.first.equals(otherLine.first) &&
                this.second.equals(otherLine.second);
    }
}

