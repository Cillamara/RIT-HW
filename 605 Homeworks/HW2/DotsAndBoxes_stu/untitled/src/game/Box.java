package game;

/**
 * A class representing a single "box" in a game of Dots and Boxes.
 * A box is a collection of four lines that define its boundaries.
 * Each box is defined by a unique upper-left coordinate
 *
 * @author Tyler Ronnenberg
 */

public class Box {

    private Line bottom;
    private Line top;
    private Line left;
    private Line right;
    private Player owner;


    /**
     * Creates a box and each line associated with that box
     * @param row Row of upper-left dot
     * @param column Column of upper-left dot
     * @param lines Lines associated with the box
     */
    public Box(int row, int column, Lines lines){
        //Upper left dot coord
        top = lines.getLine(row, column, row, column + 1);
        right = lines.getLine(row, column + 1, row + 1, column + 1);
        bottom = lines.getLine(row + 1, column, row + 1, column + 1 );
        left = lines.getLine(row, column, row + 1, column);
        owner = Player.NONE;
    }

    public int getRow() {
        return top.getFirst().getRow();
    }

    public int getColumn() {
        return top.getFirst().getColumn();
    }

    public Line getBottomLine() {
        return bottom;
    }

    public Line getTopLine() {
        return top;
    }

    public Line getLeftLine() {
        return left;
    }

    public Line getRightLine() {
        return right;
    }

    public Player getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        //Return the label of the owner of a box
        return owner.getLabel();
    }

    @Override
    public boolean equals(Object other) {

        if (!(other instanceof Box))
            return false;

        Box otherBox = (Box) (other);

        return this.getRow() == otherBox.getRow() &&
                this.getColumn() == otherBox.getColumn() &&
                this.owner == otherBox.getOwner() &&
                this.getTopLine() == otherBox.getTopLine() &&
                this.getRightLine() == otherBox.getRightLine() &&
                this.getBottomLine() == otherBox.getBottomLine() &&
                this.getLeftLine() == otherBox.getLeftLine();
    }
}
