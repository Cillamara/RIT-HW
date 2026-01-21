package hw4.heroes;

import hw4.game.Team;

import java.util.List;

/**
 * The party of heroes.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public interface Party {

    /**
     * Add a hero to the back of the collection.
     *
     * @param hero the new hero
     */
    void addHero(Hero hero);

    /**
     * Removes the hero at the front of the party
     *
     * @return the hero at the front
     */
    Hero removeHero();

    /**
     * Get the number of non-fallen heroes.
     *
     * @return number of heroes in the party
     */
    int numHeroes();

    /**
     * The team which this party is affiliated with.
     *
     * @return the team
     */
    Team getTeam();

    /**
     * Get all the undefeated heroes in the party.
     *
     * @return the party
     */
    List<Hero> getHeroes();

}
