package client;

import common.ConcentrationException;

/**
 * Represents the client-side game board for the concentration game.
 *
 * This class is the view in the MVC architecture.
 *
 * Modified from ConcentrationBoard class.
 *
 * @author RIT CS
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ConcentrationClientBoard {
    /** the smallest board is 2x2 */
    private final static int MIN_DIM = 2;
    /** the largest board is 6x6 */
    private final static int MAX_DIM = 6;

    /** the square dimension of the board */
    private final int DIM;
    /** the actual board is a 2-D grid of cards */
    private char[][] board;
    /** first card revealed in a pair. Defaults to '.' when hidden */
    private char card1;
    /** second card revealed in a pair. Defaults to '.' when hidden */
    private char card2;
    /** Boolean flag to tell the client to listen for a follow-up message */
    private boolean listenForSecond;


    /**
     * Create the board.
     *
     * @param DIM square dimension
     * @throws ConcentrationException if the dimensions are invalid
     */
    public ConcentrationClientBoard(int DIM) throws ConcentrationException {
        // check for bad dimensions
        if (DIM < MIN_DIM || DIM > MAX_DIM) {
            throw new ConcentrationException("Board size out of range: " + DIM);
        } else if (DIM % 2 != 0) {
            throw new ConcentrationException("Board size not even: " + DIM);
        }

        /**
         * Create the grid of cards.
         */
        this.DIM = DIM;
        this.board = new char[DIM][DIM];
        this.listenForSecond = false;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                this.board[i][j] = '.';
            }
        }

        // initialize card1 and card2
        this.card1 = '.';
        this.card2 = '.';
    }

    /**
     * Get the char value of a card on the board.
     * @param row row coordinate of card
     * @param col column coordinate of card
     * @return A letter if card is revealed, '.' if card is hidden
     */
    public char getCard(int row, int col) {
        return this.board[row][col];
    }

    /**
     * Reveal a hidden card.
     * Tells client to listen for second message from server if a
     *  pair of cards is revealed
     *
     * @param row the row
     * @param col the column
     * @throws ConcentrationException if the coordinate is invalid, or the
     *     card has already been revealed.
     */
    public void reveal(int row, int col, char letter) throws ConcentrationException {
        assert (row >= 0 && row < this.DIM && col >= 0 && col < this.DIM) : new ConcentrationException("Invalid coordinates");
        //If revealed card1 isn't revealed, update card1.
        this.board[row][col] = letter;
        if (card1 == '.') {
            card1 = letter;
            card2 = '.';
            if (listenForSecond) {
                listenForSecond = false;
            }
        //If card1 is revealed, update card2 and tell client to listen
        //  for second message from server
        } else if (card2 == '.') {
            card1 = '.';
            card2 = letter;
            this.listenForSecond = true;
        }
    }

    /**
     * If a mismatch message is received, client will call this to hide the
     *  two guessed cards
     * @param row1 row of first card
     * @param col1 column of first card
     * @param row2 row of second card
     * @param col2 column of second card
     * @throws ConcentrationException if either set of coordinates are invalid
     */
    public void hide(int row1, int col1, int row2, int col2) throws ConcentrationException {
        assert (row1 >= 0 && row1 < this.DIM && col1 >= 0 && col1 < this.DIM) : new ConcentrationException("Invalid coordinates");
        assert (row2 >= 0 && row2 < this.DIM && col2 >= 0 && col2 < this.DIM) : new ConcentrationException("Invalid coordinates");
        this.board[row1][col1] = '.';
        this.board[row2][col2] = '.';
        card1 = '.';
        card2 = '.';
    }

    /**
     * Get whether the client is listening for a second message or not
     * @return True if client is listening for second server message
     *          False otherwise
     */
    public boolean getListenForSecond() {
        return this.listenForSecond;
    }

    /**
     * Check if there is still at least one hidden card on the board.
     * @return True if all cards revealed, false otherwise
     */
    public boolean gameOver() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (this.board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the board, for example a
     * 4x4 game that is just underway.
     *
     *   0123
     * 0|G...
     * 1|G...
     * 2|....
     * 3|....
     *
     * @return the board as a string
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // build the top row of indices
        str.append("  ");
        for (int col=0; col<this.DIM; ++col) {
            str.append(col);
        }
        str.append("\n");
        // build each row of the actual board
        for (int row=0; row<this.DIM; ++row) {
            str.append(row).append("|");
            // build the columns of the board
            for (int col=0; col<this.DIM; ++col) {
                char card = this.board[row][col];
                // based on whether the card is hidden or not display
                // build with the correct letter
                str.append(card);
            }
            str.append("\n");
        }
        return str.toString();
    }
}
