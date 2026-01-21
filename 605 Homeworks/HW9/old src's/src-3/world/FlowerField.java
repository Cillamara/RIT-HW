package world;

import bee.Worker;

public class FlowerField {

    static int MAX_WORKERS = 10;

    private int activeWorkers;

    public FlowerField() {
        activeWorkers = 0;
    }

    public synchronized void enterField(Worker worker) {
        while (activeWorkers == MAX_WORKERS) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("*FF *" + worker + " entered the field");
        activeWorkers++;
    }

    public synchronized void exitField(Worker worker) {
        System.out.println("*FF *" + worker + " exited the field");
        activeWorkers--;
        this.notify();
    }

}
