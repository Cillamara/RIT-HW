package bee;

import world.BeeHive;
import world.FlowerField;

/**
 * The female worker bee has the task of going to the flower field and collecting
 * their assigned resource - nectar or pollen.  A worker will always get exactly 1
 * unit of their resource each time they go out to the field and when they
 * return will deposit that resource into the bee hive (as long as the hive is still
 * active). After each successful gather, the worker will sleep for the required time
 * before heading out to the fields again.
 *
 * @author RIT CS
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Worker extends Bee {
    /**
     * The resource the bee will be assigned to.  The queen and drones
     * do not collect resources, e.g. NONE.
     */
    public enum Resource {
        POLLEN,
        NECTAR,
        NONE
    }

    /** the sleep time to simulate the time it takes to gather a resource */
    public final static int WORKER_SLEEP_TIME_MS = 2000;

    /** the resource the worker will continually gather */
    private Resource resource;
    /** the field of flowers */
    private FlowerField flowerField;
    /** The hive where a worker will deposit its gathered material */
    private BeeHive beeHive;

    /**
     * Create the worker.  They need to remember their resource, as well as get
     * access to the flower field via the bee hive.
     *
     * @param resource their resource
     * @param beeHive the bee hive
     */
    protected Worker(Resource resource, BeeHive beeHive) {
        super(Role.WORKER, beeHive);
        this.resource = resource;
        //this.gathering = false;
        this.beeHive = beeHive;
        this.flowerField = beeHive.getFlowerField();
    }

    /**
     * The worker bee returns a string that contains the role, followed
     * by {@link Bee#toString()}'s string, e.g.:
     * <br>
     * <tt>{RESOURCE} ROLE #{id}</tt><br>
     * <br>
     * <br>
     * Here, {RESOURCE} is the resource, e.g. NECTAR or POLLEN,
     * ROLE is the role (should always be WORKER, but let the superclass
     * toString() figure that out), and {id} is the unique id.
     *
     * @return the string as described here
     */
    @Override
    public String toString() {
        return this.resource + " " + super.toString();
    }

    /**
     * When the worker runs, they will enter the field.  If there is
     * a free flower, they will sleep the required time to simulate
     * the extraction, then they will leave the field and deposit
     * their resource into the bee hive.  This process
     * repeats until the bee hive becomes inactive.
     */
    public void run() {
        while (beeHive.isActive()) {
            this.flowerField.enterField(this);
            try {
                this.sleep(WORKER_SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.flowerField.exitField(this);
            this.beeHive.deposit(this.resource, this);
        }
    }
}