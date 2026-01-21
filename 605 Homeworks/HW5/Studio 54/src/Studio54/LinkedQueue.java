package Studio54;

/**
 * A priority queue using a Linked List where higher priorities are placed in
 * front of lower priorities.
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class LinkedQueue {
    // size of the queue
    private int size;
    // Front of the queue
    private Node head;

    /**
     * Constructs an empty linked list
     */
    public LinkedQueue() {
        head = null;
        size = 0;
    }

    /**
     * A node for the linked list. Each node shows represents a patron in the
     * priority queue and shows who, if anyone, is behind them.
     */
    private static class Node {
        Node next;
        Patron patron;

        /**
         * Constructs a new node with the given data.
         * @param next The node(patron) behind this instance of node
         * @param patron The patron this node represents
         */
        public Node(Node next, Patron patron) {
            this.next = next;
            this.patron = patron;
        }
    }

    /**
     * Takes a patron and places them linked queue based on their priority
     * compared to the other patrons with the highest priority closes to the
     * head of the queue. The priority comparisons between patrons is made
     * using the compareTo method from the Patron class.
     *
     * @param patron the patron you wish to add into the queue
     */
    public void addPatron(Patron patron) {
        // Initializes the new patron as a node not yet in the linked queue
        Node newNode = new Node(null, patron);
        // If queue is empty
        if (head == null) {
            head = newNode;
        } else {
            // Checks front of queue's priority to that of the added patron
            if (patron.compareTo(head.patron) >= 0) {
                newNode.next = head;
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next;
                    if (patron.compareTo(current.next.patron) >= 0) {
                        newNode.next = current.next;
                        current.next = newNode;
                        break;
                    }
                }
                // Add the patron to end of queue if they're the least priority
                if (newNode.next == null) {
                    current.next = newNode;
                }
            }
        }
        size++;
    }
}

