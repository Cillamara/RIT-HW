package game;
/**
 *Creates all the lines in the game. We assume that all lines are specified
 * from left to right (horizontal) or top to bottom (vertical) For a
 * rectangular grid assume that m = rows+1 and n = columns+1. Therefore,
 * there will be 2mn - m - n lines in the grid. This site summarizes it:
 *
 * @author Liam Cui
 */

import java.util.ArrayList;


public class Lines {
    private int row;
    private int column;
    private ArrayList<Dot> dot;

    public Lines(int row, int column, ArrayList<Dot[][]> Dots) {
        this.row = row;
        this.column = column;
        dot = Dot[row][column];
    }

    public Line getLine(int row1, int column1, int row2, int column2) {
        ArrayList<Object> coordinates = new ArrayList<>();
        if (row1 < row2 || column1 < column2) {
            coordinates.add(row1);
            coordinates.add(column1);
            coordinates.add(row2);
            coordinates.add(column2);
        } else if (row2 < row1 || column2 < column1) {
            coordinates.add(row2);
            coordinates.add(column2);
            coordinates.add(row1);
            coordinates.add(column1);
        }
        return getLine(row1, column1, row2, column2);
    }
    public int size(){
        return dot.size();
    }
}
