package world;

import bee.Bee;
import bee.Bee.Role;
import bee.Queen;
import bee.Worker;
import bee.Worker.Resource;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The BeeHive is the center of the world for the bees.  It is the place where
 * the resources gathered by the workers (nectar and pollen) are stored.  These
 * resources will be used by the Queen bee who mates with her drones and
 * uses the resources in order to support reproduction.
 *
 * @author RIT CS
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class BeeHive {
    /** the field of flowers */
    private FlowerField flowerField;
    /** the queen's chamber */
    private QueensChamber queensChamber;
    /** the collection of all the bees */
    private ConcurrentLinkedQueue<Bee> bees;
    /** the collection of all the drone bees who perished mating */
    private ConcurrentLinkedQueue<Bee> perishedBees;
    /** the current amount of nectar in the bee hive */
    private int nectar;
    /** the current amount of pollen in the bee hive */
    private int pollen;
    /** the total amount of nectar gathered */
    private int nectarGathered;
    /** the total amount of pollen gathered */
    private int pollenGathered;
    /** the total number of bees that have been born */
    private int numBorn;
    /** the bees do their thing while the simulation is active */
    private boolean active;
    private Queen queen;

    /**
     * Create the bee hive.  You should create the flower field and the queen's
     * chamber here.  The bees should be created here with the suggested order
     * of the queen first, the drone bees second, and the worker bees third
     * (nectar and then pollen).
     *
     * @param numDrones number of starting drones
     * @param numNectarWorkers number of starting nectar workers
     * @param numPollenWorkers number of starting pollen workers
     */
    public BeeHive(int numDrones, int numNectarWorkers, int numPollenWorkers) {
        this.flowerField = new FlowerField();
        this.queensChamber = new QueensChamber();
        this.bees = new ConcurrentLinkedQueue<>();
        this.perishedBees = new ConcurrentLinkedQueue<>();
        this.nectar = this.pollen = 0;

        // create the bees!

        // TODO YOUR CODE HERE
        // create queen and drone bees
        this.queen = (Queen) Bee.createBee(Role.QUEEN, Resource.NONE, this);
        this.bees.add(this.queen);

        for (int i=0; i<numDrones; ++i ) {
            this.bees.add(Bee.createBee(Role.DRONE, Resource.NONE, this));
        }
        //Create the bees
        for (int i=0; i<numNectarWorkers; ++i ) {
            this.bees.add(Bee.createBee(Role.WORKER, Resource.NECTAR, this));
        }
        for (int i=0; i<numPollenWorkers; ++i ) {
            this.bees.add(Bee.createBee(Role.WORKER, Resource.POLLEN, this));
        }


        this.active = true;
        this.numBorn = this.bees.size();
        this.nectarGathered = this.pollenGathered = 0;
    }

    /**
     * Get the flower field.  Having this here reduces the amount of passing we have to do.
     * The worker bees who need the field can get it from the bee hive.
     *
     * @return the field of flowers
     */
    public FlowerField getFlowerField() {
        return this.flowerField;
    }

    /**
     * Get the queen's chamber.  The queen bee and the drones need to be aware of this.
     *
     * @return the queen's chamber
     */
    public QueensChamber getQueensChamber() {
        return this.queensChamber;
    }

    /**
     * How many total bees were born?
     *
     * @return total born
     */
    public int getNumBorn() {
        return this.numBorn;
    }

    /**
     * Get the collection of bees who perished.
     *
     * @return the perished bees
     */
    public Collection<Bee> getPerishedBees() {
        return this.perishedBees;
    }

    /**
     * How much nectar is left in the bee hive?
     *
     * @return nectar remaining
     */
    public int getRemainingNectar() {
        return this.nectar;
    }

    /**
     * How much pollen is left in the bee hive?
     *
     * @return pollen remaining
     */
    public int getRemainingPollen() {
        return this.pollen;
    }

    /**
     * How much total nectar was gathered?
     *
     * @return total nectar gathered
     */
    public int getNectarGathered() {
        return this.nectarGathered;
    }

    /**
     * How much total pollen was gathered?
     *
     * @return total pollen gathered
     */
    public int getPollenGathered() {
        return this.pollenGathered;
    }

    /**
     * Is the simulation still going?
     *
     * @return if the bee hive is active or not
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Start the simulation.  When the simulation starts, you should
     * first display the message<br>
     * <br>
     * <tt>*BH* Bee hive begins buzzing</tt><br>
     * <br>
     * Then you should start all the bees (queen, workers and drones).
     *
     * @rit.pre The bees have not started running
     * @rit.post All the bees have been signaled to begin running
     */
    public void begin() {
        System.out.println("*BH* Bee hive begins buzzing!");
        for (Bee bee : this.bees) {
            bee.start();
        }
    }

    /**
     * When the simulation ends the bee hive is no longer active.  All the
     * bees trigger off this to stop doing their normal job and shutdown.
     * At the end print out the message:<br>
     * <br>
     * <tt>*BH* Bee hive is becoming inactive!</tt>
     * <br>
     * At the end once all the bees have finished print out the message:<br>
     * <br>
     * <tt>*BH* Bee hive stops buzzing!</tt><br>
     * <br>
     *
     * @rit.pre The bees are either running or have completed their task,
     * e.g. the drones that mated have perished.
     * @rit.post All the bees have been finished running
     */
    public void end() {
        System.out.println("*BH* Bee hive is becoming inactive!");
        // flip the switch
        this.active = false;
        // TODO YOUR CODE HERE
        for (Bee bee : this.bees) {
            try {
                bee.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("*BH* Bee hive stops buzzing!");
    }

    /**
     * When a bee perishes (a drone mates with the queen), the bee hive takes
     * note of this unfortunate, but necessary event for the circle of life
     * and adds them to the collection of perished bees.
     *
     * @param bee the bee who perished
     */
    public synchronized void beePerished (Bee bee){
        this.perishedBees.add(bee);
    }

    /**
     * Whenever a bee is born after the simulation starts, they are added to
     * the beehive and should immediately start doing their task.
     *
     * @param bee the new bee
     */
    public synchronized void addBee(Bee bee) {
        this.bees.add(bee);
        if (this.isActive()) {
            bee.start();
        }
        this.numBorn++;
    }

    /**
     * If the bee hive has at least 1 unit of both nectar and pollen, it meets one
     * of the conditions by the queen for mating with a drone.
     *
     * @return do we have enough resources?
     */
    public synchronized boolean hasResources() {
        return this.nectar >= 1 && this.pollen >= 1;
    }

    /**
     * When the queen is ready to mate with a drone, they will claim 1 unit of each
     * resource.
     *
     * @rit.pre {@link BeeHive#hasResources()} is true
     */
    public synchronized void claimResources() {
        this.pollen--;
        this.nectar--;
    }

    /**
     * Add a new resource to the bee hive.  The worker bees when returning from
     * the flower field will deposit their 1 unit of either nectar or pollen
     * for storage.  At the start display the message:<br>
     * <br>
     * <tt>*BH* {bee} deposits</tt><br>
     * <br>
     *
     * @param resource the resource
     * @param bee the worker bee who deposited the resource
     *
     * @rit.pre the bee hive is still active
     */
    public synchronized void deposit(Resource resource, Worker bee) {
        System.out.println("*BH* " + bee + " deposits");
        if (resource == Resource.NECTAR) {
            this.nectar++;
            this.nectarGathered++;
        } else if (resource == Resource.POLLEN) {
            this.pollen++;
            this.pollenGathered++;
        }
    }
}
