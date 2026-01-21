package world;

import bee.Drone;
import bee.Queen;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The queen's chamber is where the mating ritual between the queen and her
 * drones is conducted. The drones will enter the chamber in order. If the
 * queen is ready and a drone is in here, the first drone will be summoned and
 * mate with the queen. Otherwise, the drone has to wait. After a drone mates
 * they perish, which is why there is no routine for exiting (like with the
 * worker bees and the flower field).
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class QueensChamber {
    /** a queue which orders the Drones by age */
    protected Queue<Drone> enterQueue;
    /** a queue which determines mating order based on the enterQueue */
    protected Queue<Drone> matingQueue;

    /**
     * Construct a new QueenChamber
     */
    public QueensChamber() {
        this.enterQueue = new LinkedBlockingQueue<>();
        this.matingQueue = new LinkedBlockingQueue<>();
    }

    /**
     * A method that can enqueue drones into the queens chamber based on their
     * birth order regardless of multiple threads running. The drone enters the
     * chamber, a message is displayed as they enter the mating queue they
     * notify next one to enter to continue on with the method. They then wait
     * until the queen summons them, and they mate for a specified time, leave,
     * then die. If the queen dismisses them, then the drone just leaves.
     *
     * @param drone The drone that enters the chamber
     */
    public synchronized void enterChamber(Drone drone) {
        //Enter the chamber in order
        while (enterQueue.peek() != drone) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("*QC* " + drone + " enters chamber");
        //Enter the mating queue
        matingQueue.add(enterQueue.poll());
        //Notify Queen
        this.notifyAll();
        //Wait until summoned
        while (!drone.getMated() && drone.getActive()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //Drone mates
        if (drone.getMated()) {
            try {
                Thread.sleep(Queen.SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //Drone leaves
        matingQueue.remove();
        System.out.println("*QC* " + drone + " leaves chamber");
        }

    /**
     * A method called by the queen to choose the next drone in the matingQueue
     * to mate with the queen. This method displays which drone has mated with
     * the queen and allows the mated drone to go on with its life (death).
      */
    public synchronized void summonDrone() {
        Drone matingDrone = this.matingQueue.peek();
        matingDrone.setMated();
        System.out.println("*QC* Queen mates with " + matingDrone);
        this.notifyAll();
    }

    /**
     * A method called by the queen when the simulation is over. This method
     * tells the first drone in the mating queue that mating season is over.
     * This method is called by the queen until no drones remain.
     */
    public synchronized void dismissDrone() {
        if (!this.matingQueue.isEmpty()) {
            this.matingQueue.peek().deactivate();
        }
        this.notifyAll();
    }

    /**
     * Is the matingQueue empty?
     * @return boolean for whether the matingQueue is empty.
     */
    public boolean hasDrone() {
        return !matingQueue.isEmpty();
    }
}
