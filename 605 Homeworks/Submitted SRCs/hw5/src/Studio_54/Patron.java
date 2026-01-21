package Studio_54;

/**
 * Class representing a patron of Studio54.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Patron implements Comparable<Patron> {

    /**
     * Name of the patron
     */
    private final String name;
    /**
     * Coolness value of the patron
     */
    private final int coolness;
    /**
     * Is the patron regular or not
     */
    private final boolean regular;

    /**
     * Constructor for a patron object
     * @param name Name of the patron
     * @param coolness Integer value between 1 and 10
     * @param regular True if patron is a regular, false otherwise
     */
    public Patron(String name, int coolness, boolean regular) {
        this.name = name;
        this.coolness = coolness;
        if (coolness < 1 || coolness > 10)
            throw new AssertionError("coolness must be between 1 and 10");
        this.regular = regular;
    }

    /**
     * Compares two patrons and returns integer
     * value based on priority difference
     *
     * @param o the patron to be compared
     * @return Positive integer if param patron is lower priority,
     *          zero if objects are equal priority,
     *          negative integer if param patron is higher priority
     */
    @Override
    public int compareTo(Patron o) {
        int comparison = this.coolness - o.coolness;
        if (comparison == 0) {
            if (this.regular == o.regular) {
                return 0;
            } else if (!this.regular) {
                return -1;
            } else {
                return 1;
            }
        } else
            return comparison;
    }

    /**
     * Get the name of a patron
     * @return name of the patron
     */
    public String getName() {
        return name;
    }

    /**
     * Get coolness of a patron.
     * @return integer value of coolness
     */
    public int getCoolness() {
        return coolness;
    }

    /**
     * Check if a patron is regular or not
     * @return boolean value of regular field
     */
    public boolean isRegular() {
        return regular;
    }

    /**
     * Displays patron regularity, name, and coolness
     *
     * @return String representation of patron
     */
    public String toString() {
        if (regular) {
            return "Regular " + name + " with coolness factor " + coolness;
        } else {
            return name + " with coolness factor " + coolness;
        }
    }

}
