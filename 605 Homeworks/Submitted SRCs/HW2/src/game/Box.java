package game;

import java.util.ArrayList;

/**
 * A class representing a single "box" in a game of Dots and Boxes.
 * A box is a collection of four lines that define its boundaries.
 * Each box is defined by a unique upper-left coordinate
 *
 * @author Tyler Ronnenberg
 * @auther Liam Cui
 */

public class Box {

    /** Fields for the bottom, top, left, and right lines of the box. */
    private Line bottom;
    private Line top;
    private Line left;
    private Line right;
    /** Fields for the row and column */
    private int row;
    private int column;
    /** Fields for dot and the owner*/
    private Dot dot;
    private Player owner;

    /**
     * Creates a box and each line associated with that box
     * @param row Row of upper-left dot
     * @param column Column of upper-left dot
     * @param lines Lines associated with the box
     */
    public Box(int row, int column, Lines lines){
        //Upper left dot coord
        this.row = row;
        this.column = column;
        this.dot = new Dot(row, column);
        top = lines.getLine(row, column, row, column + 1);
        right = lines.getLine(row, column + 1, row + 1, column + 1);
        bottom = lines.getLine(row + 1, column, row + 1, column + 1 );
        left = lines.getLine(row, column, row + 1, column);
        top.setBox(this);
        right.setBox(this);
        bottom.setBox(this);
        left.setBox(this);
        owner = Player.NONE;
    }

    /**
     * Get the row.
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the column.
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get the upper-left dot of the box
     * @return upper-left dot of the box
     */
    public Dot getDot() {
        return dot;
    }

    /**
     * Get bottom line.
     * @return the bottom line.
     */
    public Line getBottomLine() {
        return bottom;
    }

    /**
     * Get top line.
     * @return the top line
     */
    public Line getTopLine() {
        return top;
    }

    /**
     * Get left line.
     * @return the left line
     */
    public Line getLeftLine() {
        return left;
    }

    /**
     * Get right line.
     * @return the right line
     */
    public Line getRightLine() {
        return right;
    }

    /**
     * Get the owner of the box.
     * @return the owner, game.Player.NONE if not owned.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Attempt to claim a box. A box is claimed by an owner when all 4 lines
     * that form the box have been claimed. This is called each time a Line is
     * claimed. The line knows which boxes are associated with it. Based on
     * these assumptions, and the prevention of a line being claimed multiple
     * times, this should only be called exactly 4 times per Box.
     * @param owner the owner that claimed the last line.
     */
    public void claim(Player owner) {
        if (this.getTopLine().hasOwner()
                && this.getRightLine().hasOwner()
                && this.getBottomLine().hasOwner()
                && this.getLeftLine().hasOwner()){
            this.owner = owner;
        }
    }

    /**
     * Returns the label of the box's owner. Look at the Player enum.
     * @return the label of the box's Owner
     */
    @Override
    public String toString() {
        //Return the label of the owner of a box
        return owner.getLabel();
    }

    /**
     * Two boxes are equal if the boxes have the same row, column, owner and 4 lines.
     * @param other the box to compare with
     * @return whether they are equal or not
     */
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
