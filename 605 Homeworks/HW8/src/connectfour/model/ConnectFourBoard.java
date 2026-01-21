package connectfour.model;

import java.util.LinkedList;
import java.util.List;

/**
 * The model for the Connect Four game.
 *
 * @author RIT CS
 */
public class ConnectFourBoard {
    /** the number of rows */
    public final static int ROWS = 6;
    /** the number of columns */
    public final static int COLS = 7;
    /** how big a line one needs to win */
    public final static int WIN_LEN = 4;

    /** used to determine which players turn it is, and the pieces on the board */
    public enum Player {
        P1,
        P2,
        NONE
    }

    /** the game status */
    public enum Status {
        NOT_OVER,
        P1_WINS,
        P2_WINS,
        TIE
    }

    /** whose turn is it to make the next move? */
    private Player currentPlayer;
    /** how many moves have been made? */
    private int movesMade;
    /** the status of the game */
    private Status status;
    /** the observers of this model */
    private List<Observer<ConnectFourBoard>> observers;
    /** the game board */
    private Player[][] board;
    /** the last column a piece was placed - used for win checking */
    private int lastCol;
    /** the row the last piece was placed - used for win checking. */
    private int lastRow;

    /**
     * Create a new board.
     */
    public ConnectFourBoard() {
        this.currentPlayer = Player.P1;
        this.movesMade = 0;
        this.status = Status.NOT_OVER;
        this.board = new Player[ROWS][COLS];
        // initialize the board with no pieces claimed
        for (int row=0; row<ROWS; ++row) {
            for (int col=0; col<COLS; ++col) {
                this.board[row][col] = Player.NONE;
            }
        }
        // add this observer so we can notify them of updates
        this.observers = new LinkedList<>();
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<ConnectFourBoard> observer) {
        this.observers.add(observer);
    }

    /** When the model changes, the observers are notified via their update() method */
    private void notifyObservers() {
        for (Observer<ConnectFourBoard> obs: this.observers ) {
            obs.update(this);
        }
    }

    /**
     * Get the current player whose turn it is.
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    /**
     * Get the number of moves that have been made.
     *
     * @return moves made
     */
    public int getMovesMade() {
        return this.movesMade;
    }

    /**
     * Get the status of the game.
     *
     * @return game status
     */
    public Status getGameStatus() {
        return this.status;
    }

    public Player getContents(int row, int col) {
        return this.board[row][col];
    }

    /**
     * Is this a valid move?
     *
     * @param col the column
     * @return true iff the column is not full
     */
    public boolean isValidMove(int col) {
        return (col >= 0 && col < COLS) &&
                (this.board[0][col] == Player.NONE);
    }

    /**
     * Make a move by placing a disc in a valid column.
     *
     * @rit.pre the move must be valid
     * @param col the column the current player is placing their piece into
     */
    public void makeMove(int col) {
        ++this.movesMade;

        // find first open row from bottom up
        for (int row=ROWS-1; row >= 0; --row) {
            if (board[row][col] == Player.NONE) {
                this.board[row][col] = this.currentPlayer;
                this.lastRow = row;
                this.lastCol = col;
                break;
            }
        }

        // check if the game has been won, tied, or is still going on
        if (hasWonGame()) {
            this.status = this.currentPlayer == Player.P1 ? Status.P1_WINS : Status.P2_WINS;
        } else if (this.movesMade == ROWS * COLS) {
            this.status = Status.TIE;
        } else {
            // toggle player if game not over
            this.currentPlayer = (this.currentPlayer == Player.P1) ? Player.P2 : Player.P1;
        }

        // let the view know the moves has been made
        notifyObservers();
    }

    /**
     * This class is used to represent both directions (see {@link #DIRS})
     * and coordinate locations on the board. It is non-static so that
     * the {@link CRPair#inBounds()} and {@link CRPair#contents()} methods
     * can access the board inside an instance.
     */
    private class CRPair {
        /* column or column delta; row or row delta */
        public final int r, c;

        /**
         * Initialize (immutable)
         *
         * @param r the row
         * @param c the column
         */
        public CRPair(int r, int c) { this.r = r; this.c = c; }

        /**
         * Return a new CRPair representing this location offset by
         * a scale times a vector
         *
         * @param scale the scale factor for the offset
         * @param dir   the direction, or vector, of the offset
         * @return new CRPair representing the computed location
         */
        public CRPair advance(int scale, CRPair dir) {
            return new CRPair(this.r + scale * dir.r,this.c + scale * dir.c);
        }

        /**
         * Is this location in bounds?
         *
         * @return true iff the coordinates are at least zero and
         * both less than the board's upper bounds
         */
        public boolean inBounds() {
            return this.r >= 0 && this.r < ROWS && this.c >= 0 && this.c < COLS;
        }

        /**
         * Get the player whose disc occupies this location.
         *
         * @return an instance of the Move (&quot;player&quot;) enumeration
         */
        public Player contents() {
            return board[this.r][ this.c];
        }
    }

    /**
     * All eight directions in which an N-in-a-row could be discovered.
     * Because of how {@link #hasWonGame()} works, half the directions
     * have been eliminated.
     */
    private List< CRPair > DIRS = List.of(
            new CRPair( 1, -1 ),
            new CRPair( 1, 0 ),
            new CRPair( 0, 1 ),
            new CRPair( 1, 1 )
    );

    /**
     * For each direction, look for WIN_LEN consecutive pieces on either side
     * of most recent move.
     *
     * @return True if the game is in a winning state. False otherwise.
     */
    public boolean hasWonGame() {
        CRPair start = new CRPair(lastRow, lastCol);
        Player here = start.contents();
        for ( CRPair dir : DIRS ) {
            int sequence = 0;
            // look on both sides of the move along the direction
            for (int delta = 1 - WIN_LEN; delta < WIN_LEN; ++delta) {
                CRPair next = start.advance(delta, dir);
                if (!next.inBounds()) {
                    continue; // skip if the current piece is out of bounds
                }

                // if the piece matches, increment; otherwise reset seq count
                sequence = (next.contents() == here) ? sequence + 1 : 0;

                if ( sequence >= WIN_LEN ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a string representation of the board, suitable for printing out.
     * The starting board would be:<br>
     * <br><tt>
     *   0  1  2  3 4 5 6<br>
     * 0[.][.][.][.][.][.][.]<br>
     * 1[.][.][.][.][.][.][.]<br>
     * 2[.][.][.][.][.][.][.]<br>
     * 3[.][.][.][.][.][.][.]<br>
     * 4[.][.][.][.][.][.][.]<br>
     * 5[.][.][.][.][.][.][.]<br>
     * </tt>
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // build the top row with column numbers
        builder.append(' ');
        for (int c = 0; c < COLS; ++c) {
            builder.append(" " + c + ' ');
        }
        builder.append('\n');

        // build remaining rows with row numbers and column values
        for (int row = 0; row < ROWS; ++row) {
            builder.append(row);
            for (int col = 0; col < COLS; ++col) {
                builder.append('[');
                if (this.board[row][col] == Player.NONE) {
                    builder.append(".");
                } else if (this.board[row][col] == Player.P1) {
                    builder.append("O");
                } else {
                    builder.append("X");
                }
                builder.append(']');
            }
            builder.append('\n');
        }

        return builder.toString();
    }
}
