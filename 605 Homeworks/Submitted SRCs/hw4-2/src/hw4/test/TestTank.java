/*
 * file: TestTank.java
 */

package hw4.test;

import hw4.game.Team;
import hw4.heroes.Hero;
import hw4.heroes.Heroes.Role;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * A test class for the tanks.
 *
 * @author RIT CS
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTank {
    /** dragon rank */
    private static Hero dragonTank;

    /** lion rank */
    private static Hero lionTank;

    /** Used to test that expected System.out print's happen */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeClass
    public static void init() {
        dragonTank = new TestParty(Team.DRAGON).getHeroes().get(2);
        lionTank = new TestParty(Team.LION).getHeroes().get(2);
    }
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
    }

    @Test
    public void test1Init() {
        assertEquals("Dragon Tank name",
                "Smaug",
                dragonTank.getName());
        assertEquals("Lion Tank name",
                "Aslan",
                lionTank.getName());
        assertEquals("Dragon Tank not fallen",
                false,
                dragonTank.hasFallen());
        assertEquals("Dragon Tank role",
                Role.TANK,
                dragonTank.getRole());
        assertEquals("Dragon Tank toString",
                "Smaug, TANK, 40/40",
                dragonTank.toString());
        assertEquals("Lion Tank toString",
                "Aslan, TANK, 40/40",
                lionTank.toString());
    }

    @Test
    public void test2Attack() {
        dragonTank.attack(lionTank);
        assertEquals("Dragon Tank attacks Lion Tank",
                "Aslan takes 13 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Tank takeDamage",
                "Aslan, TANK, 27/40",
                lionTank.toString());
    }

    @Test
    public void test3Heal() {
        lionTank.heal(10);
        assertEquals("Lion Tank heal message",
                "Aslan heals 10 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Tank toString",
                "Aslan, TANK, 37/40",
                lionTank.toString());
    }

    @Test
    public void test4Overheal() {
        lionTank.heal(20);
        assertEquals("Lion Tank heal message",
                "Aslan heals 20 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Tank toString",
                "Aslan, TANK, 40/40",
                lionTank.toString());
    }

    @Test
    public void test5Fall() {
        lionTank.takeDamage(50);
        assertEquals("Lion Tank falls to damage",
                "Aslan takes 45 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Tank toString",
                "Aslan, TANK, 0/40",
                lionTank.toString());
        assertEquals("Lion Tank has fallen", true, lionTank.hasFallen());
    }
}
