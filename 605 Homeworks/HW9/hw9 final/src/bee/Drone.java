package bee;

import world.BeeHive;
import world.QueensChamber;

/**
 * The male drone bee has a tough life.  His only job is to mate with the queen
 * by entering the queen's chamber and awaiting his royal highness for some
 * sexy time.  Unfortunately his reward from mating with the queen is his
 * endophallus gets ripped off, and he perishes soon after mating.
 *
 * @author RIT CS
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Drone extends Bee {

    /** The chamber of the queen where drones goes to mate */
    private final QueensChamber queensChamber;
    /** Boolean flag showing if a drone has mated */
    private boolean mated;
    /** Boolean flag showing if a drone is currently actively trying to mate. */
    private boolean active;

    /**
     * When the drone is created they should retrieve the queen's
     * chamber from the bee hive and initially the drone has not mated.
     *
     * @param beeHive the bee hive
     */
    protected Drone(BeeHive beeHive){
        super(Role.DRONE, beeHive);
        this.queensChamber = beeHive.getQueensChamber();
        this.mated = false;
        this.active = true;
    }

    /** Return whether a drone has mated */
    public boolean getMated() {
        return mated;
    }
    /** Flag the drone as having mated */
    public void setMated() {
        this.mated = true;
    }
    /** Return whether a drone is waiting to mate*/
    public boolean getActive() {
        return active;
    }
    /** Tell the drone to stop trying to mate.
     * Used by the queen to dismiss the drones at the end of the simulation.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * When the drone runs, they check if the bee hive is active. If so,
     * they perform their sole task of entering the queen's chamber.
     * If they return from the chamber, it can mean only one of two
     * things.  If they mated with the queen, they sleep for the
     * required mating time, and then perish (the beehive should be
     * notified of this tragic event).  You should display a message:<br>
     * <br>
     * <tt>*D* {bee} has perished!</tt><br>
     * <br>
     * <br>
     * Otherwise if the drone has not mated it means they survived the
     * simulation, and they should end their run without any
     * sleeping.
     */
    public void run() {
        if (beeHive.isActive()) {
            this.queensChamber.enterChamber(this);
            if (this.mated) {
                this.beeHive.beePerished(this);
                System.out.println("*D* " + this + " has perished!");
            }
        }
    }
}