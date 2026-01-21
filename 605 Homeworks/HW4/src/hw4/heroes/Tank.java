package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

public class Tank extends Hero {
    protected Tank(Team team) {
        super(team == Team.DRAGON ? DRAGON_TANK:LION_TANK, 40);
        this.setRole(Heroes.Role.TANK);
    }



    @Override
    public void attack(Hero target) {
        target.takeDamage(15);
    }
}
