package hw4.game;

import hw4.heroes.*;
import hw4.heroes.Hero.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A party is a collection of non-fallen allies that represent a team. The
 * behavior of this collection is FIFO (a queue). The hero at the front of the
 * queue is next up for a battle round. If a hero is not defeated when battling
 * in a round, they will be added to the back of the queue so they may combat
 * again in the future.
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class HeroParty extends Object implements Party{
    private final Team team;
    private final List<Hero> heroes;

    /**
     * Create the party. Here we associate the team with the party. We then add
     * the heroes in the following order: Berserker, Healer and Tank. The
     * collection is then shuffled using the random number generator seed
     * value. To shuffle the collection of heroes (assumed to be either an
     * ArrayList or LinkedList):
     *
     * @param team the team
     * @param seed the random number generator seed
     */
    public HeroParty(Team team, int seed){
        this.team = team;
        List<Hero> initialHeroes = new ArrayList<>();
        initialHeroes.add(Hero.create(Heroes.Role.BERSERKER, team, null)); // Berserker
        initialHeroes.add(Hero.create(Heroes.Role.HEALER, team, this));    // Healer (requires Party)
        initialHeroes.add(Hero.create(Heroes.Role.TANK, team, null));
        Collections.shuffle(initialHeroes, new Random(seed));
        this.heroes = new LinkedList<>(initialHeroes);
    }

    /**
     * Add a hero to the back of the collection.
     *
     * @param hero the new hero
     */
    public void addHero(Hero hero) {
        heroes.add(hero);
    }

    /**
     *Remove the hero at the front of the collection.
     *
     * @return the hero at the front
     */
    public Hero removeHero(){
        return Hero.remove(0);
    }

    /**
     *Get the number of non-fallen heroes.
     *
     * @return number of heroes in the party
     */
    public int numHeroes() {
        return heroes.size;
    }

    /**
     *The team which this party is affiliated with.
     *
     * @return the team
     */
    public Team getTeam() {
        return team;
    }

    /**
     *Get all the undefeated heroes in the party.
     *
     * @return the party
     */
    public List<Hero> getHeroes() {
        return new LinkedList<>(heroes);
    }

    /**
     * Returns a string representation of the party.
     *
     * @return the string
     */
    @Override
    public String toString() {
        String teamName = team.name().charAt(0) + team.name().substring(1).toLowerCase() + "s";
        StringBuilder sb = new StringBuilder(teamName).append(":\n");
        for (Hero hero : heroes) {
            sb.append(hero.toString()).append("\n");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
    }
}
