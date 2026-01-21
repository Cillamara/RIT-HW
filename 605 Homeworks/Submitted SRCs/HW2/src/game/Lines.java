package game;

/**
 * A class representing all the lines in a game of Dots and Boxes.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Lines {

    /** Arrays for storing horizontal and vertical lines */
    private Line[][] horizontalLines;
    private Line[][] verticalLines;
    /** Number of lines in a game board */
    public static int numOfLines;

    /**
     * Creates all the lines in the game
     * All line owners default to NONE
     * @param rows The number of m in a game
     * @param columns The number of columns in a game
     * @param dots The collection of dots making up the game board
     */
    public Lines(int rows, int columns, Dot[][] dots) {
        //Calculate number of lines
        int m = rows+1;
        int n = columns+1;
        numOfLines = (2 * m * n) - m - n;

        //Initialize dots array
        dots = new Dot[m][n];
        //Populate dots
        for (int row = 0; row < m; row++) {
            for (int column = 0; column < n; column++) {
                dots[row][column] = new Dot(row, column);
            }
        }

        //Initialize arrays for horizontal and vertical lines
        horizontalLines = new Line[m][n-1];
        verticalLines = new Line[m-1][n];
        //Create lines using elements of dots and store in an array
        for (int row = 0; row < m; row++) {
            for (int column = 0; column < n; column++) {
                //Horizontal lines
                if (column < n - 1)
                    horizontalLines[row][column] =
                            new Line(dots[row][column], dots[row][column+1]);
                //Vertical lines
                if (row < m - 1)
                    verticalLines[row][column] =
                            new Line(dots[row][column], dots[row+1][column]);
            }
        }
    }

    /**
     * Get the number of lines in a collection
     * @return An integer value of the number of lines
     */
    public int size(){
        return numOfLines;
    }

    /**
     * Get a line by its coordinates.
     * Must be specified left-to-right, top-to-bottom or it won't be found
     * @param row1 Row of the first dot
     * @param row2 Row of the second dot
     * @param column1 Column of the first dot
     * @param column2 Column of the second dot
     * @return Null if invalid coordinates entered
     */
    public Line getLine(int row1, int column1, int row2, int column2) {
        if (row1 == row2)
            return horizontalLines[row1][column1];
        if (column1 == column2)
            return verticalLines[row1][column1];
        else
            return null;
    }
}
