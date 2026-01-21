package hw4.heroes;

import hw4.game.Team;

import java.util.List;

public interface Party {

    public void addHero(Hero hero);
    public Hero removeHero();
    public int numHeroes();
    public Team getTeam();
    public List<Hero> getHeroes();
}
