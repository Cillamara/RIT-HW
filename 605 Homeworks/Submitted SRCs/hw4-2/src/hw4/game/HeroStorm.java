package hw4.game;

import hw4.heroes.Hero;

import java.util.Scanner;

/**
 * The main class for the RPG game, Super Fantasy Hero Storm. This class deals
 * with the main game playing. The program is run on the command line as:
 * $ java HeroStorm dragon_seed_# lion_seed_#
 * Here, the seeds are two integers used to seed the random number generators
 * when shuffling the two teams of 3 heroes.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class HeroStorm {

    // Field for the round counter
    private int round;
    // Fields for the dragon and lion parties
    private HeroParty dragonParty;
    private HeroParty lionParty;

    /**
     * Create the parties and initialize the round counter.
     *
     * @param dragonSeed the seed for the dragon random number generator
     * @param lionSeed the seed for the dragon random number generator
     */
    public HeroStorm(int dragonSeed, int lionSeed) {
        this.dragonParty = new HeroParty(Team.DRAGON, dragonSeed);
        this.lionParty = new HeroParty(Team.LION, lionSeed);
        round = 1;
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
        //Play proceeds while neither party is empty
        while (dragonParty.numHeroes() != 0 && lionParty.numHeroes() != 0) {
            //Display current party status
            System.out.println("\n" + "Battle #" + round);
            System.out.println("==========");
            System.out.println(dragonParty.toString() + "\n");
            System.out.println(lionParty.toString() + "\n");
            // Grab first hero from each party and display a vs message
            Hero dragonActive = dragonParty.removeHero();
            Hero lionActive = lionParty.removeHero();
            //Odd rounds = Dragon attacks first
            if (round % 2 == 1) {
                System.out.println("*** " + dragonActive.getName() + " vs " +lionActive.getName() + "!" + "\n");
                //Dragon hero attacks first
                dragonActive.attack(lionActive);
                //If lion hero hasn't died, attack back
                // and add to end of party list
                if (!lionActive.hasFallen()) {
                    lionActive.attack(dragonActive);
                    lionParty.addHero(lionActive);
                }
                //If dragon hero is still alive, return to end of party list
                if (!dragonActive.hasFallen()) {
                    dragonParty.addHero(dragonActive);
                }
                //Increase round count
                round++;
            } else {
                System.out.println("*** " + lionActive.getName() + " vs " +
                        dragonActive.getName() + "!" + "\n");
                //Lion hero attacks first
                lionActive.attack(dragonActive);
                //If dragon hero hasn't died, attack back
                // and add to end of party list
                if (!dragonActive.hasFallen()) {
                    dragonActive.attack(lionActive);
                    dragonParty.addHero(dragonActive);
                }
                //If lion hero is still alive, return to end of party list
                if (!lionActive.hasFallen()) {
                    lionParty.addHero(lionActive);
                }
                //Increase round count
                round++;
            }
        }
        if (dragonParty.numHeroes() != 0 && lionParty.numHeroes() == 0) {
            System.out.println("Team Dragon wins!");
        } else {
            System.out.println("Team Lion wins!");
        }
    }

    /**
     * The main method. It checks the number of command line arguments, then
     * creates and plays the game.
     *
     * @param args the command line arguments, two integers for the dragon and
     *             lion random number generator seeds
     */
    public static void main(String[] args) {
        //Check for the correct number of arguments
        if (args.length != 2) {
            System.out.println("Usage: java HeroStorm " +
                    "dragon_seed_# lion_seed_#");
        }

        //Read the seed values from the command line
        int dragonSeed = Integer.parseInt(args[0]);
        int lionSeed = Integer.parseInt(args[1]);
        System.out.println("dragon_seed: " + dragonSeed);
        System.out.println("lion_seed: " + lionSeed);
        //Create instance of game HeroStorm
        HeroStorm hs = new HeroStorm(dragonSeed, lionSeed);
        //Play the game
        hs.play();
    }

}
