package game;

import java.util.ArrayList;

/**
 * A class representing a single line in a game of Dots and Boxes
 * A valid line is defined by two coordinates that are exactly one unit apart,
 * either horizontally or vertically.
 * A line must be defined left to right, or top to bottom
 *
 * @author Tyler Ronnenberg
 */

public class Line {

    /**
     * String representations of different lines
     */
    public static final String EMPTY = "";
    public static final String HORI_LINE = "-";
    public static final String VERT_LINE = "|";

    public static int numOfMoves = 0;

    private Player player;

    /**
     * First and second dots. Defined by constructor
     */
    private final Dot first;
    private final Dot second;

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
    }

    public static int getNumOfMoves() {
        return numOfMoves;
    }

    public Dot getFirst(){
        //Return the first dot's coordinates
        return first;
    }

    public Dot getSecond(){
        //Return the second dot's coordinates
        return second;
    }

    public Player getOwner(){
        return player;
    }

    public ArrayList<Box> getBoxes(){
        //TODO Get boxes associated with a line
        ArrayList<Box> boxes = new ArrayList<>();
        int firstRow = first.getRow();
        int firstColumn = first.getColumn();
        int secondRow = second.getRow();
        int secondColumn = second.getColumn();
        Dot[][] dots = new Dot[secondRow][secondColumn];
        Lines testLines = new Lines(secondRow, secondColumn, dots);
        Box boxToAdd = new Box(firstRow, firstColumn, testLines);
        if (first.equals(boxToAdd.getTopLine().first) && second.equals(boxToAdd.getTopLine().second))
            boxes.add(boxToAdd);
        if (first.equals(boxToAdd.getRightLine().first) && second.equals(boxToAdd.getRightLine().second))
            boxes.add(boxToAdd);
        if (first.equals(boxToAdd.getBottomLine().first) && second.equals(boxToAdd.getBottomLine().second))
            boxes.add(boxToAdd);
        if (first.equals(boxToAdd.getLeftLine().first) && second.equals(boxToAdd.getLeftLine().second))
            boxes.add(boxToAdd);


        return boxes;
    }

    public ArrayList<Box> getBoxes2(){
        ArrayList<Box> boxes = new ArrayList<>();
        Dot[][] dots;
        for (int row = 0; row < second.getRow(); row++) {
            for (int column = 0; column < second.getColumn(); column++) {
                boxes.add(Box box(i, j, Lines line(i, j, Dot[][] dots)))
            }
        }
        return boxes;
    }

    public boolean hasOwner(){
        return player != Player.NONE;
    }

    public void claim(Player owner){
        //TODO Claim a line and any boxes completed
        if (!this.hasOwner())
            this.player = owner;
        numOfMoves++;
    }

    public void setBox(Box box){
        //TODO Set a line with a box it is associated with


    }

    public String lineDebug(){
        int r1 = first.getRow();
        int c1 = first.getColumn();
        int r2 = second.getRow();
        int c2 = second.getColumn();
        return "Line between ( " + r1 + " , " + c1 + " ) and ( " + r2 + " , " + c2 + " )";
    }

    @Override
    public String toString(){
        if (first.getRow() == second.getRow()) {
            return HORI_LINE;
        } else if (first.getColumn() == second.getColumn()) {
            return VERT_LINE;
        } else {
            return EMPTY;
        }
    }

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

