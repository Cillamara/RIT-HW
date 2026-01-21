package hw4.heroes;

import hw4.game.Team;

import static hw4.heroes.Heroes.*;

public class Healer extends Hero {

    private Party party;
    private Team team;

    public Healer(Team team, Party party) {
        super(team == Team.DRAGON ? DRAGON_HEALER : LION_HEALER, 35);
        this.setRole(Heroes.Role.HEALER);
        this.party = party;
        this.team = team;
    }

    @Override
    public void attack(Hero target) {
        //Heal self, then heal all living party members
        this.heal(10);
        for (Hero healTarget : party.getHeroes()) {
            if (!healTarget.hasFallen() && healTarget != this) {
                healTarget.heal(10);
            }
        }
        target.takeDamage(10);
    }

}
