package game;

import java.lang.reflect.Array;

public enum Player {
    BLUE,
    NONE,
    RED;


    /*
    game.Player player = game.Player.RED;        // player is Red
    if (player == game.Player.Red) {
    System.out.println(player);              // Prints: RED
    System.out.println(player.getLabel());   // Prints: R
    }
    player = game.Player.BLUE;                   // player is now Blue
    */

    /**
     * Gets the player's label
     * @return A string containing the player's label
     */
    public String getLabel() {
        if (this == BLUE) {
            return "B";
        } else if (this == RED) {
            return "R";
        } else {
            return "N";
        }
    }

}
