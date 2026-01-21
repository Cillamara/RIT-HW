package connectfour.gui;

import connectfour.model.ConnectFourBoard;
import connectfour.model.Observer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * The graphical UI for Connect Four. This class encapsulates both
 * the View and Controller portions of the MVC architecture.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ConnectFourGUI extends Application implements Observer<ConnectFourBoard> {

    /** Constants for controlling game settings  */
    private final static int BOARD_WIDTH = 575;
    private final static int BOARD_HEIGHT = 500;
    private final static int ROWS = ConnectFourBoard.ROWS;
    private final static int COLUMNS = ConnectFourBoard.COLS;
    private final static int LABEL_FONT_SIZE = 22;

    /** The game board */
    private ConnectFourBoard board;
    /** Label for showing an invalid move */
    private Label invalidMove;
    /** Label for number of moves made */
    private Label movesMade;
    /** Label for whose turn it is */
    private Label currentPlayer;
    /** Label for game status */
    private Label status;
    /** Array of buttons corresponding to board positions */
    private PlayButton[][] buttons;

    /**
     * Initializes game window and constructs javafx elements
     */
    public ConnectFourGUI() {
        this.board = new ConnectFourBoard();
        this.invalidMove = new Label();
        this.movesMade = new Label();
        this.currentPlayer = new Label();
        this.status = new Label();
        this.buttons = new PlayButton[ROWS][COLUMNS];
        initializeView();
    }

    /** Add this GUI object as an observer to the board object */
    private void initializeView() {
        this.board.addObserver(this);
    }

    /**
     * Method for updating the GUI as the game is played.
     * @param board Model which is updated and reflected in the view of the MVC
     */
    @Override
    public void update(ConnectFourBoard board) {
        //Place disc on bottom-most free slot in column
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                this.buttons[row][col].setPlayer(this.board.getContents(row, col));
            }
        }
        //Update labels
        this.movesMade.setText(this.board.getMovesMade() + " moves made");
        this.currentPlayer.setText("Current Player: " + this.board.getCurrentPlayer().toString());
        this.status.setText("Status: " + this.board.getGameStatus().toString());
        //Disable buttons when game is over
        if (!this.board.getGameStatus().equals(ConnectFourBoard.Status.NOT_OVER)) {
            for (Button[] buttonRow : this.buttons) {
                for (Button button : buttonRow) {
                    button.setDisable(true);
                }
            }
        }
    }

    /**
     * Factory method for creating the game board
     * @return GridPane object of empty slots
     */
    private GridPane makeGridPane() {
        GridPane gridPane = new GridPane();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                PlayButton square = new PlayButton();
                int col = j;
                this.buttons[i][j] = square;
                //Set action for a button press
                square.setOnAction(event -> {
                    //If move is valid, make a move
                    if (this.board.isValidMove(col)) {
                        this.invalidMove.setText("");
                        this.board.makeMove(col);
                    //If move is invalid display message "Invalid move"
                    } else {
                        this.invalidMove.setText("Invalid move");
                    }
                });
                //Add button to grid
                gridPane.add(square, j, i);
            }
        }
        return gridPane;
    }

    /**
     * Class for a button corresponding to a space on the game board.
     */
    private static class PlayButton extends Button {

        /** The player that owns the space */
        private ConnectFourBoard.Player player;
        /** Images for displaying space ownership */
        private final Image EMPTY = new Image(this.getClass().getResourceAsStream("empty.png"));
        private final Image PLAYER1 = new Image(this.getClass().getResourceAsStream("p1black.png"));
        private final Image PLAYER2 = new Image(this.getClass().getResourceAsStream("p2red.png"));

        /**
         * Construct a space on the game board and set its owner to NONE.
         * Set graphic to display an open space
         */
        public PlayButton() {
            this.player = ConnectFourBoard.Player.NONE;
            this.setGraphic(new ImageView(EMPTY));
        }

        public ConnectFourBoard.Player getPlayer() {
            return player;
        }

        /**
         * Method to change the graphic of a space when a player claims it.
         * @param player Player who has taken the space
         */
        public void setPlayer(ConnectFourBoard.Player player) {
            this.player = player;
            if (player == ConnectFourBoard.Player.NONE) {
                this.setGraphic(new ImageView(EMPTY));
            } else if (player == ConnectFourBoard.Player.P1) {
                this.setGraphic(new ImageView(PLAYER1));
            } else {
                this.setGraphic(new ImageView(PLAYER2));
            }
        }
    }

    /**
     * Method to set up and run the Connect Four GUI.
     * Game window is a VBox object with three sub-fields:
     * ________________
     * |  Game board  |
     * ----------------
     * | invalid move |
     * ----------------
     * |  game info   |
     * ----------------
     * @param stage the primary stage for the game.
     */
    @Override
    public void start(Stage stage) {
        //Initialize game window layout
        //Use stack of three boxes to contain elements
        VBox mainPane = new VBox();
        GridPane playArea = makeGridPane();
        BorderPane labelPane = new BorderPane();
        //Build game board and set its position
        mainPane.getChildren().add(playArea);

        //Populate Labels and format/position them
        //Invalid move label
        this.invalidMove.setText("");
        this.invalidMove.setStyle("-fx-font: " + LABEL_FONT_SIZE/1.5 + " arial");

        //Add invalid move label above info label section
        mainPane.getChildren().add(this.invalidMove);
        //Add info label pane on bottom of window
        mainPane.getChildren().add(labelPane);
        //Align game board to center of window
        playArea.setAlignment(Pos.CENTER);

        //Moves made label
        this.movesMade = new Label(this.board.getMovesMade() + " moves made");
        this.movesMade.setStyle("-fx-font: " + LABEL_FONT_SIZE + " arial");
        labelPane.setLeft(this.movesMade);
        //Current player label
        this.currentPlayer = new Label("Current Player: " + this.board.getCurrentPlayer().toString());
        this.currentPlayer.setStyle("-fx-font: " + LABEL_FONT_SIZE + " arial");
        labelPane.setCenter(this.currentPlayer);
        //Status label
        this.status = new Label("Status: " + this.board.getGameStatus());
        this.status.setStyle("-fx-font: " + LABEL_FONT_SIZE + " arial");
        labelPane.setRight(this.status);

        //Align all elements to center of window
        mainPane.setAlignment(Pos.TOP_CENTER);

        //Set game icon and title
        stage.getIcons().add(new Image(getClass().getResourceAsStream("p1black.png")));
        stage.setTitle("ConnectFourGUI");
        //Display game window
        Scene scene = new Scene(mainPane, BOARD_WIDTH, BOARD_HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (Exception e) {
            System.out.println(e);;
        }
    }
}
