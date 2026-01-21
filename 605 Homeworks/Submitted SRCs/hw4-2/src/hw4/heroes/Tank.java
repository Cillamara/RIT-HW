package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

/**
 * Represents the hardy Tank class.
 * Uses their strong defense to withstand devastating blows!
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Tank extends Hero {

    //Hero team
    private Team team;

    /**
     * Constructs a tank hero object and sets its team.
     * Starting HP is 40
     *
     * @param team Sets the team of the Tank
     */
    protected Tank(Team team) {
        super(team == Team.DRAGON ? DRAGON_TANK:LION_TANK, 40);
        this.setRole(Role.TANK);
        this.team = team;
    }


    /**
     * Attacks the target for 15 damage
     *
     * @param target Hero to be attacked
     */
    @Override
    public void attack(Hero target) {
        target.takeDamage(15);
    }
}
