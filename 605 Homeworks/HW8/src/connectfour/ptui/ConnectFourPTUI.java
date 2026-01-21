package connectfour.ptui;

import connectfour.model.ConnectFourBoard;
import connectfour.model.ConnectFourBoard.Status;
import connectfour.model.Observer;

import java.util.Scanner;

/**
 * The plain text UI for Connect Four.  This class encapsulates both
 * the View and Controller portions of the MVC architecture.
 *
 * @author RIT CS
 */
public class ConnectFourPTUI implements Observer<ConnectFourBoard> {
    /** the board is the Model */
    private ConnectFourBoard board;

    /**
     * Create the PTUI.
     */
    public ConnectFourPTUI() {
        this.board = new ConnectFourBoard();
        initializeView();
    }

    /*
     ******************* THE VIEW SECTION ***************************************
     */

    /**
     * The View initialization adds ourselves as an observer to the Model
     */
    private void initializeView() {
        this.board.addObserver(this);
    }

    /**
     * The update for the PTUI prints the board and some state.
     */
    @Override
    public void update(ConnectFourBoard board) {
        System.out.print(this.board);
        System.out.println("Moves made: " + this.board.getMovesMade());
        System.out.println("Current player: " + this.board.getCurrentPlayer());
        System.out.println("Status: " + this.board.getGameStatus());

    }

    /*
     ******************* THE CONTROLLER SECTION *********************************
     */

    /**
     * The run loop prompts for user input and makes calls into the Model.
     */
    public void run() {
        System.out.println("Connect Four PTUI\n");
        update(this.board);
        try (Scanner in = new Scanner( System.in )) {
            System.out.print("\ncolumn (-1 to exit): ");
            while (this.board.getGameStatus() == Status.NOT_OVER) {
                int col = in.nextInt();
                if (col == -1) {
                    return ;
                } else if (this.board.isValidMove(col)) {
                    this.board.makeMove(col);
                } else {
                    System.out.println("Invalid column.");
                }
                System.out.print("\nEnter column (-1 to exit): ");
            }
        }
    }

    /**
     * The main routine.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        ConnectFourPTUI ptui = new ConnectFourPTUI();
        ptui.run();
    }
}
