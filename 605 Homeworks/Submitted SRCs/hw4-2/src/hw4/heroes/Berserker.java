package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

/**
 * Represents the fierce berserker class.
 * A glass-cannon warrior who deals big damage!
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Berserker extends Hero {

    //Hero team
    private Team team;

    /**
     * Constructs a berserker hero object and sets its team.
     * Starting HP is 30
     *
     * @param team Sets the team of the berserker
     */
    protected Berserker(Team team) {
        super(team == Team.DRAGON ? DRAGON_BERSERKER:LION_BERSERKER, 30);
        this.setRole(Heroes.Role.BERSERKER);
        this.team = team;
    }

    /**
     * Berserkers deal 20 damage to their targets
     *
     * @param target Hero to be attacked
     */
    @Override
    public void attack(Hero target) {
        //Deal 20 attack damage
        target.takeDamage(20);
    }
}
