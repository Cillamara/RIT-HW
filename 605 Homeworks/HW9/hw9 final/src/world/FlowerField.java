package world;

import bee.Worker;

/**
 * The field of flowers that are ripe for the worker bees to
 *  gather the nectar and pollen resources.
 * The bees can arrive in any order and they are immediately allowed to start
 *  gathering, as long as there is a free flower.
 * Otherwise the bee must wait until a flower becomes free.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class FlowerField {

    /** Represents the number of flowers available for gathering */
    static int MAX_WORKERS = 10;

    /** Number of active worker bees */
    private int activeWorkers;

    /**
     * Create the flower field. Initially there are no worker bees in the field.
     */
    public FlowerField() {
        activeWorkers = 0;
    }

    /**
     * Method for allowing workers entry to the field.
     * If there are no available flowers, the worker bee will wait until one
     *  becomes available
     * @param worker The worker who will attempt to enter the field
     */
    public synchronized void enterField(Worker worker) {
        while (activeWorkers == MAX_WORKERS) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("*FF* " + worker + " entered the field");
        activeWorkers++;
    }

    /**
     * Method for worker bees leaving the field. A worker will let one of the
     *  waiting workers know it is free to enter.
     * @param worker Worker leaving the field
     */
    public synchronized void exitField(Worker worker) {
        System.out.println("*FF* " + worker + " exited the field");
        activeWorkers--;
        this.notify();
    }
}
