package test;

import Studio_54.Patron;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A unit test for the Studio_54.Patron Class
 *
 * @author Tyler Ronnenberg & Liam Cui
 */

public class TestPatron {
    @Test(expected = AssertionError.class)
    public void testBadCoolness() {
        new Patron("Jim", 0, true);
        new Patron("Jim", 11, true);
    }

    @Test
    public void testPatron() {
        Patron patron = new Patron("Jim", 5, false);
        assertEquals("Jim", patron.getName());
        assertEquals(5, patron.getCoolness());
        assertFalse(patron.isRegular());
    }




}
