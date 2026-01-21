/*
 * file: TestBerserker.java
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
 * A test class for the beserkers.
 *
 * @author RIT CS
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestBerserker {
    /** dragon berserker */
    private static Hero dragonBerserker;

    /** lion beserker */
    private static Hero lionBerserker;

    /** Used to test that expected System.out print's happen */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeClass
    public static void init() {
        dragonBerserker = new TestParty(Team.DRAGON).getHeroes().get(0);
        lionBerserker = new TestParty(Team.LION).getHeroes().get(0);
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
        assertEquals("Dragon Berserker name",
                "Trogdor",
                dragonBerserker.getName());
        assertEquals("Lion Berserker name",
                "Simba",
                lionBerserker.getName());
        assertEquals("Dragon Berserker not fallen",
                false,
                dragonBerserker.hasFallen());
        assertEquals("Dragon Berserker role",
                Role.BERSERKER,
                dragonBerserker.getRole());
        assertEquals("Dragon Berserker toString",
                "Trogdor, BERSERKER, 30/30",
                dragonBerserker.toString());
        assertEquals("Lion Berserker toString",
                "Simba, BERSERKER, 30/30",
                lionBerserker.toString());
    }

    @Test
    public void test2Attack() {
        dragonBerserker.attack(lionBerserker);
        assertEquals("Dragon Berserker attacks Lion Berserker",
                "Simba takes 20 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Berserker takeDamage",
                "Simba, BERSERKER, 10/30",
                lionBerserker.toString());
    }

    @Test
    public void test3Heal() {
        lionBerserker.heal(10);
        assertEquals("Lion Berserker heal message",
                "Simba heals 10 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Berserker toString",
                "Simba, BERSERKER, 20/30",
                lionBerserker.toString());
    }

    @Test
    public void test4Overheal() {
        lionBerserker.heal(20);
        assertEquals("Lion Berserker heal message",
                "Simba heals 20 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Berserker toString",
                "Simba, BERSERKER, 30/30",
                lionBerserker.toString());
    }

    @Test
    public void test5Fall() {
        lionBerserker.takeDamage(50);
        assertEquals("Lion Berserker falls to damage",
                "Simba takes 50 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Berserker toString",
                "Simba, BERSERKER, 0/30",
                lionBerserker.toString());
        assertEquals("Lion Berserker has fallen", true, lionBerserker.hasFallen());

    }
}
