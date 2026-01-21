import bee.Bee;
import world.BeeHive;

import java.util.Collection;

/**
 * The main program for the bee simulation.<br>
 * <br>
 * It is run on the command line with four arguments:<br>
 * <br>
 * <tt>$ java BeeMain seconds drones nectar_workers pollen_workers</tt>
 *
 * @author RIT CS
 */
public class BeeMain {

    /** how long the simulation will run (in milliseconds) */
    private final int SIMULATION_TIME_MS;
    /** the bee hive */
    private BeeHive beeHive;

    /**
     * Create the BeeMain.  It sets the simulation time, displays some starting
     * statistics, and then creates the bee hive.
     *
     * @param simulationTime simulation time in seconds
     * @param numDrones number of starting drones
     * @param numNectarWorkers number of starting nectar workers
     * @param numPollenWorkers number of starting pollen workers
     */
    public BeeMain(int simulationTime, int numDrones, int numNectarWorkers, int numPollenWorkers) {
        this.SIMULATION_TIME_MS = simulationTime * 1000; // convert from s to ms

        System.out.println("Simulation time: " + simulationTime + " seconds");
        System.out.println("Starting drones: " + numDrones);
        System.out.println("Starting nectar workers: " + numNectarWorkers);
        System.out.println("Starting pollen workers: " + numPollenWorkers);

        this.beeHive = new BeeHive(numDrones, numNectarWorkers, numPollenWorkers);
    }

    /**
     * Run the simulation
     */
    private void go() {
        // tell the bees to start
        this.beeHive.begin();

        // wait for the simulation time to expire
        try {
            Thread.sleep(SIMULATION_TIME_MS);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        // tell the bees the party is over, and then display some final statistics
        this.beeHive.end();
        statistics();
    }

    /**
     * Display the end of simulation statistics.
     */
    private void statistics() {
        System.out.println("\nSTATISTICS");
        System.out.println("==========");
        System.out.println("Bees born: " + this.beeHive.getNumBorn());

        Collection<Bee> perished = this.beeHive.getPerishedBees();
        System.out.println("Bees perished: " + perished.size());
        for (Bee bee : perished) {
            System.out.println("\t" + bee);
        }

        System.out.println("Nectar gathered: " + this.beeHive.getNectarGathered());
        System.out.println("Pollen gathered: " + this.beeHive.getPollenGathered());
        System.out.println("Nectar remaining: " + this.beeHive.getRemainingNectar());
        System.out.println("Pollen remaining: " + this.beeHive.getRemainingPollen());
    }

    /**
     * The main method reads the command line arguments, constructs BeeMain, and
     * then starts the simulation.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java BeeMain seconds drones nectar_workers pollen_workers");
        } else {
            new BeeMain(
                    Integer.parseInt(args[0]), // seconds
                    Integer.parseInt(args[1]), // #drones
                    Integer.parseInt(args[2]), // #nectar_workers
                    Integer.parseInt(args[3])  // #pollen_workers
            ).go();
        }
    }
}
