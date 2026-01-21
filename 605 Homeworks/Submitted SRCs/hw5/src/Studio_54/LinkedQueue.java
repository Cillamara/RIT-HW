package Studio_54;

/**
 * A priority queue using a Linked List where higher priorities are placed in
 * front of lower priorities.
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class LinkedQueue<T extends Comparable<T>> implements PriorityQueue<T> {
    /**
     * Size of the queue
     */
    private int size;
    /**
     * Front of the queue
     */
    private LinkedNode<T> head;

    /**
     * Constructs an empty linked list
     */
    public LinkedQueue() {
        head = null;
        size = 0;
    }

    /**
     * A node for the linked queue. Each node contains a patron's data in the
     * priority queue and shows who, if anyone, is behind them.
     */
    private static class LinkedNode<T> {
        LinkedNode<T> next;
        T data;

        /**
         * Constructs a new node with the given data.
         * @param next The node(patron) behind this instance of node
         * @param data The patron this node represents
         */
        public LinkedNode(T data, LinkedNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    /**
     * Takes a patron and places them linked queue based on their priority
     * compared to the other patrons with the highest priority closes to the
     * head of the queue. The priority comparisons between patrons is made
     * using the compareTo method from the Patron class.
     *
     * @param data the data you wish to add into the queue
     */
    public void enqueue(T data) {
        // Initializes the new data as a node not yet in the linked queue
        LinkedNode<T> newNode = new LinkedNode<>(data, null);
        // If queue is empty set head pointer to new node
        if (isEmpty()) {
            head = newNode;
        } else {
            // Checks front of queue priority to the new node
            if (data.compareTo(head.data) > 0) {
                newNode.next = head;
                head = newNode;
            } else {
                LinkedNode<T> current = head;
                while (current.next != null) {
                    if (data.compareTo(current.next.data) > 0) {
                        newNode.next = current.next;
                        current.next = newNode;
                        break;
                    }
                    current = current.next;
                }
                // Add the new node to end of queue if they're the least priority
                if (newNode.next == null) {
                    current.next = newNode;
                }
            }
        }
        size++;
    }

    /**
     * Returns the data of the patron in the front of the line and makes the
     * patron behind them the new front of the line.
     *
     * @return data of the patron who is admitted from the front of the line.
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        } else {
            T entrant = head.data;
            head = head.next;
            size--;
            return entrant;
        }
    }

    /**
     * Getter for getting first person in line's data. Used during testing.
     *
     * @return head patron data
     */
    public T getHead() {
        return head.data;
    }

    /**
     * Getter for retrieving size of the linked queue.
     *
     * @return size of the list
     */
    public int getSize() {
        return size;
    }

    /**
     * Determines whether the linked queue is empty or not.
     * @return Boolean of whether linked queue is empty or not
     */
    public boolean isEmpty() {
        return size == 0;
    }
}