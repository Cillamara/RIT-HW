package world;

import bee.Bee;
import bee.Drone;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueensChamber {

    protected ConcurrentLinkedQueue<Drone> droneQueue;

    protected ConcurrentLinkedQueue<Drone> beginningQueue;

    public final String lock = "lock";

    public QueensChamber() {
        this.beginningQueue = new ConcurrentLinkedQueue<>();
        this.droneQueue = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<Drone> getBeginningQueue() {
        return beginningQueue;
    }

    public ConcurrentLinkedQueue<Drone> getDroneQueue() {
        return droneQueue;
    }

    public void enterChamber(Drone drone) {
        synchronized (lock) {
            //Queue drone
            this.droneQueue.add(drone);
            System.out.println("*QC* " + drone + " enters chamber");
        }
    }

    public void summonDrone() {
        Drone matingDrone = this.droneQueue.poll();
        matingDrone.setMated();
        System.out.println("*QC* Queen mates with " + matingDrone);
        synchronized (this.lock) {
            this.lock.notifyAll();
        }
    }

    public synchronized void dismissDrone() {
        if (!this.droneQueue.isEmpty()) {
            Drone removedDrone = this.droneQueue.poll();
            removedDrone.setRemoved();
        }
    }

    public boolean hasDrone() {
        return !droneQueue.isEmpty();
    }
}
