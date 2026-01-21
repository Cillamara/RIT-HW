package hw4.game;

import hw4.heroes.Heroes.*;

public class HeroStorm {
    /**
     * fields
     */
    private final HeroParty dragonParty;
    private final HeroParty lionParty;
    private int round;

    /**
     * Create the parties and initialize the round counter.
     *
     * @param dragonSeed
     * @param lionSeed
     */
    public HeroStorm(int dragonSeed, int lionSeed) {
        this.dragonParty = new HeroParty(Team.DRAGON, dragonSeed);
        this.lionParty = new HeroParty(Team.LION, lionSeed);
        this.round = 1;
    }

    /**
     * The game is played in battle rounds. A round is one attack between the
     * "front" heroes of the two teams who are temporarily removed from the
     * party. The first hero to attack alternates by round, starting with Team
     * Dragon. If the hero who is attacked is not defeated, they can attack the
     * first hero back. Afterward, each non-defeated hero is added to the back
     * of their party. Defeated heroes merely "disappear" with a farewell
     * message about having fallen. The rounds continue until one of the teams
     * has all of their members defeated. The other team is declared the
     * winner. There is no interaction by the user in this game. Refer to the
     * sample outputs for details on the output formatting.
     */
    public void play() {
        while (dragonParty.numHeroes() > 0 && lionParty.numHeroes() > 0) {
            System.out.println("Round " + round);

            hw4.heroes.Hero dragonHero = dragonParty.removeHero();
            hw4.heroes.Hero lionHero = lionParty.removeHero();

            hw4.heroes.Hero firstAttacker;
            hw4.heroes.Hero secondAttacker;

            if (round % 2 == 1) { // Dragon attacks first on odd rounds
                firstAttacker = dragonHero;
                secondAttacker = lionHero;
            } else { // Lion attacks first on even rounds
                firstAttacker = lionHero;
                secondAttacker = dragonHero;
            }

            // First attack
            firstAttacker.attack(secondAttacker);

            // Counterattack if defender survived
            if (!secondAttacker.hasFallen()) {
                secondAttacker.attack(firstAttacker);
            }

            // Return surviving heroes to their parties
            if (!dragonHero.hasFallen()) {
                dragonParty.addHero(dragonHero);
            } else {
                System.out.println(dragonHero.getName() + " has fallen");
            }

            if (!lionHero.hasFallen()) {
                lionParty.addHero(lionHero);
            } else {
                System.out.println(lionHero.getName() + " has fallen");
            }

            round++;
        }

        // Determine the winner
        if (dragonParty.numHeroes() == 0 && lionParty.numHeroes() == 0) {
            System.out.println("All heroes have fallen. It's a draw!");
        } else if (dragonParty.numHeroes() == 0) {
            System.out.println("Team Lions wins!");
        } else {
            System.out.println("Team Dragons wins!");
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java HeroStorm <dragonSeed> <lionSeed>");
            System.exit(1);
        }
        try {
            int dragonSeed = Integer.parseInt(args[0]);
            int lionSeed = Integer.parseInt(args[1]);
            HeroStorm game = new HeroStorm(dragonSeed, lionSeed);
            game.play();
        } catch (NumberFormatException e) {
            System.err.println("Seeds must be integers.");
            System.exit(1);
        }
    }

}
