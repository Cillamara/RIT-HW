package Studio_54;

import java.util.Queue;

/**
 * Interface defining a queue data structure with priority sorting
 *
 * @param <T> Comparable data type
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public interface PriorityQueue<T extends Comparable<T>> {

    public void enqueue(T element);
    public T dequeue();
    public boolean isEmpty();
    
}
