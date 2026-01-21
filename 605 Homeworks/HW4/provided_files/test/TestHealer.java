/*
 * file: TestHealer.java
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
 * A test class for the healers.
 *
 * @author RIT CS
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHealer {
    /** dragon healer */
    private static Hero dragonHealer;

    /** lion healer */
    private static Hero lionHealer;

    /** Used to test that expected System.out print's happen */
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeClass
    public static void init() {
        dragonHealer = new TestParty(Team.DRAGON).getHeroes().get(1);
        lionHealer = new TestParty(Team.LION).getHeroes().get(1);
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
        assertEquals("Dragon Healer name",
                "Spyro",
                dragonHealer.getName());
        assertEquals("Lion Healer name",
                "Elsa",
                lionHealer.getName());
        assertEquals("Dragon Healer not fallen",
                false,
                dragonHealer.hasFallen());
        assertEquals("Dragon Healer role",
                Role.HEALER,
                dragonHealer.getRole());
        assertEquals("Dragon Healer toString",
                "Spyro, HEALER, 35/35",
                dragonHealer.toString());
        assertEquals("Lion Healer toString",
                "Elsa, HEALER, 35/35",
                lionHealer.toString());
    }

    @Test
    public void test2Attack() {
        dragonHealer.attack(lionHealer);
        assertEquals("Dragon Healer attacks Lion Healer",
                "Spyro heals 10 points" + System.getProperty("line.separator") +
                "Trogdor heals 10 points" + System.getProperty("line.separator") +
                "Spyro heals 10 points" + System.getProperty("line.separator") +
                "Smaug heals 10 points" + System.getProperty("line.separator") +
                "Elsa takes 10 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Healer takeDamage",
                "Elsa, HEALER, 25/35",
                lionHealer.toString());
    }

    @Test
    public void test3Heal() {
        lionHealer.heal(10);
        assertEquals("Lion Healer heal message",
                "Elsa heals 10 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Healer toString",
                "Elsa, HEALER, 35/35",
                lionHealer.toString());
    }

    @Test
    public void test4Overheal() {
        lionHealer.heal(20);
        assertEquals("Lion Healer heal message",
                "Elsa heals 20 points" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Healer toString",
                "Elsa, HEALER, 35/35",
                lionHealer.toString());
    }

    @Test
    public void test5Fall() {
        lionHealer.takeDamage(50);
        assertEquals("Lion Healer falls to damage",
                "Elsa takes 50 damage" + System.getProperty("line.separator"),
                outContent.toString());
        assertEquals("Lion Healer toString",
                "Elsa, HEALER, 0/35",
                lionHealer.toString());
        assertEquals("Lion Healer has fallen", true, lionHealer.hasFallen());
    }
}
