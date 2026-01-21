package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

public class Berserker extends Hero {

    private Team team;

    /**
     *
     * @param team
     */
    public Berserker(Team team) {
        super(team == Team.DRAGON ? DRAGON_BERSERKER:LION_BERSERKER, 30);
        this.setRole(Heroes.Role.BERSERKER);
        this.team = team;
    }

    @Override
    public void attack(Hero target) {
        //Deal 20 attack damage
        System.out.println(this.getName() + " attacks " + target.getName());
        target.takeDamage(20);
    }
}
