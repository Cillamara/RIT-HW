package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

/**
 * Represents the righteous healer class.
 * Heals their party members before inflicting damage on their foe.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Healer extends Hero {

    //Hero party
    private Party party;
    //Hero team
    private Team team;

    /**
     * Constructs a healer hero object and sets its team.
     * Starting HP is 35
     *
     * @param team Sets the team of the healer
     * @param party Sets the party of the healer
     */
    protected Healer(Team team, Party party) {
        super(team == Team.DRAGON ? DRAGON_HEALER:LION_HEALER, 35);
        this.setRole(Role.HEALER);
        this.team = team;
        this.party = party;
    }

    /**
     * Healer heals self and any living party members for 10 before attacking
     * for 10
     *
     * @param target Hero to be attacked
     */
    @Override
    public void attack(Hero target) {
        //Heal self, then heal all living party members
        this.heal(10);
        for (Hero healTarget : party.getHeroes()) {
            if (!healTarget.hasFallen()) {
                healTarget.heal(10);
            }
        }
        target.takeDamage(10);
    }

}
