package world;

import bee.Drone;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueensChamber {

    protected Queue<Drone> droneQueue;


    public QueensChamber() {
        this.droneQueue = new ConcurrentLinkedQueue<>();
    }

    public synchronized void enterChamber(Drone drone) {
        //Queue drone
        this.droneQueue.add(drone);
        System.out.println("*QC* " + drone + " enters chamber");
        while (!drone.getRemoved() && !drone.getMated()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //Drone leaves
        System.out.println("*QC* " + drone + " leaves chamber");
        }

    public synchronized void summonDrone() {
        Drone matingDrone = this.droneQueue.poll();
        matingDrone.setMated();
        System.out.println("*QC* Queen mates with " + matingDrone);
        this.notifyAll();
    }

    public synchronized void dismissDrone() {
        Drone removedDrone = this.droneQueue.poll();
        removedDrone.setRemoved();
        this.notifyAll();
    }

    public boolean hasDrone() {
        return !droneQueue.isEmpty();
    }
}
