package util;

import java.util.Random;

/**
 * An unseeded random number generator that is suitable for generating random
 * numbers in an inclusive range provided by the caller.
 *
 * @author RIT CS
 */
public class RandomBee {
    /** Random number generator usable by all classes */
    private static Random rand = new Random();

    /**
     * Generate a random integer between min and max inclusive.  For example: <br>
     * <br>
     * <tt>BeeRandom.nextInt(1, 5): A random number, 1-5</tt><br>
     * <br>
     *
     * @param min the smallest value allowed.
     * @param max the largest value allowed.
     * @return A random integer
     */
    public static int nextInt(int min, int max) {
        return rand.nextInt(max - min + 1) + min;
    }
}
