package battleship;

import java.io.*;

/**
 * Class representing the single player battleship game.
 * The main function takes a filepath as an argument and determines whether it
 * is a binary save file which continues a game or a text file which starts a
 * new game.
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class Battleship implements Serializable {

    /** CONSTANTS for print statements */
    public static final String ALL_SHIPS_SUNK = "All Ships Sunk!";
    public static final String BAD_ARG_COUNT =
            "Wrong number of arguments for command";
    public static final String DIM_TOO_BIG =
            "Board dimensions too big to display.";
    public static final String HIT = "h";
    public static final int MAX_DIM = 20;
    public static final String PROMPT = "> ";
    public static final String QUIT = "q";
    public static final String REVEAL = "!";
    public static final String SAVE = "s";
    public static final String WHITESPACE = "\\s+";
    /** The board where the game is played */
    public Board gameBoard;

    /**
     * Using instructions from a txt file, constructs a new Battleship Object
     * which represents a new game of Battleship.
     * @param filename A txt file that instructs the creation of a new Object
     * @throws battleship.BattleshipException If the init configuration file
     *  is incorrect
     * @throws IOException If there are errors reading the file
     */
    public Battleship(String filename) throws battleship.BattleshipException,
            IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        // Read board dimensions from first line of input file and create blank
        // game board
        String dimLine = br.readLine();
        String[] dims = dimLine.split(WHITESPACE);
        int rows = Integer.parseInt(dims[0]);
        int columns = Integer.parseInt(dims[1]);
        if (rows > MAX_DIM || columns > MAX_DIM) {
            throw new battleship.BattleshipException(DIM_TOO_BIG);
        }
        this.gameBoard = new Board(rows, columns);
        //Read ship positions, orientation, and length from the remaining lines
        String shipData;
        while ((shipData = br.readLine()) != null) {
            String[] ship = shipData.split(WHITESPACE);
            int shipRow = Integer.parseInt(ship[0]);
            int shipCol = Integer.parseInt(ship[1]);
            Orientation shipOrt = Orientation.valueOf(ship[2]);
            int shipLength = Integer.parseInt(ship[3]);
            Ship shipToCreate = new Ship(gameBoard, shipRow, shipCol, shipOrt,
                    shipLength);
            gameBoard.ships.add(shipToCreate);
        }
    }

    /**
     * Displays the board of the current game at the start and after each
     * player input. While there are still ships to be sunk, the method will
     * accept a player input. The inputs are: "help" which lists all available
     * commands as inputs, "h" followed by row then column coordinates to hit
     * a cell on the board, "s" followed by a file path to save the current
     * game as an object binary file of the current battleship object, "!"
     * which displays all cells hit and the remaining not hit ship cells, "q"
     *  which quits the game. The game ends when all ships are sunk.
     * @throws OutOfBoundsException If a hit move is played outside the board.
     * @throws CellPlayedException If a hit move has already been played.
     * @throws IOException If an invalid input is made.
     */
    public void play() throws OutOfBoundsException, CellPlayedException,
            IOException {
        gameBoard.display(System.out);
        while (!gameBoard.allSunk()) {
            System.out.print(PROMPT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    System.in));
            String command = reader.readLine();
            String[] orders = command.split(WHITESPACE);
            switch (orders[0]) {
                case "help" -> {
                    System.out.println("""
                        Available commands:
                        h row column - Hit a cell.
                        s file - Save game state to file.
                        ! - Reveal all ship locations.
                        q - Quit game.""");
                    gameBoard.display(System.out);
                }
                // hit a cell
                case HIT -> {
                        try {
                            gameBoard.getCell(Integer.parseInt(orders[1]),
                                    Integer.parseInt(orders[2])).hit();
                            gameBoard.display(System.out);
                        } catch (CellPlayedException | OutOfBoundsException e)
                        {   System.out.println(e);
                        } catch (Exception e) {
                            System.out.println(BAD_ARG_COUNT + ": h");
                        }
                }
                // save the game state to a bin file of your choice
                case SAVE -> {
                    try (FileOutputStream fos = new FileOutputStream(
                            command.substring(2) + ".bin")) {
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        oos.writeObject(this);
                    } catch (Exception e) {
                        System.out.println(
                                "Please enter a file name: s filename");
                    }
                }
                // reveal all
                case REVEAL -> {
                    gameBoard.fullDisplay(System.out);
                }
                // quit the game
                case QUIT -> System.exit(1);
                default -> System.out.println("Unknown command. Try again.");
            }
        }
        //win the game
        System.out.println(ALL_SHIPS_SUNK);
    }

    /**
     * The main method.
     * Reads the file given from the command line argument and determines
     * whether it is a binary save file which stores a battleship object which
     * is accessed through object input stream and then its play method is run
     * to continue the game. If it is not a save then it is read as a text file
     * that gives instructions to create a new Battleship object and run that
     * play method to start a new game.
     * @param args the filepath as a command line argument
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Battleship setup-file");
            System.exit(1);
        }
        String file = args[0];
        Battleship gameFromSave;
        //Attempt to read save file
        boolean readAsTxt = true;
        System.out.print("Checking if " + file + " is a saved game file...");
        // if it is a save, play the game from the save
        try (ObjectInputStream saveFile = new ObjectInputStream(new
                FileInputStream(args[0]))){
            gameFromSave = (Battleship) saveFile.readObject();
            System.out.print(" yes\n");
            gameFromSave.play();
            readAsTxt = false;
        } catch (IOException | ClassNotFoundException e)
        {   System.out.print(" no; will be read as a text setup file.\n");
        } catch (BattleshipException be) {
            System.out.println(be);
        }
        // if it is a txt, make a new game
        if (readAsTxt) {
            try {
                Battleship game = new Battleship(file);
                game.play();
            } catch (IOException | BattleshipException e) {
                System.out.println(e);
            }
        }
    }
}

