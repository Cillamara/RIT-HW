package test;

import Studio_54.LinkedQueue;
import Studio_54.Patron;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertSame;

/**
 * A test for the LinkedQueue Class
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class TestLinkedQueue {

    Patron patron1 = new Patron("Jim", 4, true);
    Patron patron2 = new Patron("Yanis", 5, false);
    Patron patron3 = new Patron("Takashi", 6, true);
    Patron patron4 = new Patron("Lucas", 6, false);


    @Test
    public void testPatronsLinkedQueue() {
        LinkedQueue<Patron> queue = new LinkedQueue<>();
        // add patrons
        queue.enqueue(patron1);
        queue.enqueue(patron2);
        queue.enqueue(patron3);
        queue.enqueue(patron4);

        // test getHead()
        assertSame(patron3, queue.getHead());

        // test getSize()
        assertEquals(4, queue.getSize());

        // test dequeue order
        assertSame(patron3, queue.dequeue());
        assertSame(patron4, queue.dequeue());
        assertSame(patron2, queue.dequeue());
        assertSame(patron1, queue.dequeue());

        // test isEmpty()
        assertTrue(queue.isEmpty());
    }
}
