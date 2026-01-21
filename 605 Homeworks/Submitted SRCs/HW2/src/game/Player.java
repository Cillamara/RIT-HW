package game;

import java.lang.reflect.Array;

/**
 * This class is an enumeration for representing the players in the Dots And
 * Boxes game.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public enum Player {
    /**
     * Enum constants are the Owner types available; player BLUE and RED as
     * well as NONE for when no owner is assigned.
     */
    BLUE,
    NONE,
    RED;

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
            return " ";
        }
    }

}
