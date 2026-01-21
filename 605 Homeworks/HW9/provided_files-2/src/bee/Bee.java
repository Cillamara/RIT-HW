package bee;

import world.BeeHive;

/**
 * The base class for all the different types of bees.  All bees have a role that
 * identified them, e.g. a drone who mates with the queen, the royal queen herself,
 * and the worker bees.  Each bee when created gets a unique ID that starts at 1
 * (the queen bee).  This allows us to uniquely identify each bee.  This class
 * uses the factory method pattern to create a bee, so the caller does not
 * need to be aware of the subclass constructors.  For example, to create
 * a pollen worker bee (assume beeHive is the bee hive):<br>
 * <br>
 * <tt>Bee.createBee(Bee.Role.WORKER, Resource.POLLEN, beeHive)</tt><br>
 * <br>
 *
 * @author RIT CS
 * @author YOUR NAME HERE
 */
public abstract class Bee extends Thread {
    /** The three roles */
    public enum Role {
        DRONE,
        QUEEN,
        WORKER,
    }

    /** the unique id to assign to the next bee created */
    private static int ID = 1;

    /** the beehive is protected so that the subclasses can access it directly */
    protected BeeHive beeHive;

    /** the id of the bee */
    private int id;
    /** the role of the bee */
    private Role role;

    /**
     * The factory method for creating a bee of a specific role.  It displays
     * a message after creating the new bee:<br>
     * <br>
     * <tt>*BH* {bee} is born</tt><br>
     * <br>
     *
     * @param role the bee's role
     * @param resource the bee's resource (only applicable to worker bees)
     * @param beeHive the bee hive
     *
     * @return the new bee
     */
    public static Bee createBee(Role role, Worker.Resource resource, BeeHive beeHive) {
        // TODO YOUR CODE HERE
        return null;
    }

    /**
     * The actual constructor for a bee for setting the bee's role, their id,
     * and associating them with the bee hive.
     *
     * @param role their role
     * @param beeHive the bee hive
     */
    protected Bee(Role role, BeeHive beeHive) {
        this.role = role;
        this.id = ID++;
        this.beeHive = beeHive;
    }

    /**
     * Get the bee's role.
     *
     * @return role
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Return a string representation of the bee in the format:<br>
     * <br>
     * <tt>{ROLE} #{id}</tt><br>
     * <br>
     * Here, <tt>{ROLE}</tt> is the bee's role, e.g. DRONE, QUEEN or WORKER,
     * and {id} is the unique id of the bee.
     *
     * @return the string described here
     */
    @Override
    public String toString() {
        return this.role + " #" + this.id;
    }

    /**
     * Two bees are equal if they have the same id.
     *
     * @param other the other thing to compare with
     *
     * @return whether they are equal or not
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof Bee) {
            Bee bee = (Bee) other;
            result = this.id == bee.id;
        }
        return result;
    }

    /**
     * Since all bee's have unique id's, their hash code is just their id.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return this.id;
    }
}
