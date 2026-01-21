/*
 * file: Heroes.java
 */

package hw4.heroes;

import hw4.game.Team;

/**
 * Public data about all the heroes including the roles and unique names
 * based on team and role type.
 *
 * @author RIT CS
 */
public class Heroes {
    /**
     * The three kinds of heroes
     */
    public enum Role {
        BERSERKER,
        HEALER,
        TANK
    }

    /**
     * Dragon berserker name
     */
    public final static String DRAGON_BERSERKER = "Trogdor";
    /**
     * Dragon healer name
     */
    public final static String DRAGON_HEALER = "Spyro";
    /**
     * Dragon tank name
     */
    public final static String DRAGON_TANK = "Smaug";

    /**
     * Lion berserker name
     */
    public final static String LION_BERSERKER= "Simba";
    /**
     * Lion healer name
     */
    public final static String LION_HEALER = "Elsa";
    /**
     * Lion tank name
     */
    public final static String LION_TANK = "Aslan";

    /**
     * Get the name of the hero based on their role and team.
     *
     * @param team hero's team
     * @param role hero's role
     * @return name of hero
     */
    public static String getName(Team team, Role role) {
        if (team == Team.DRAGON) {
            if (role == Role.BERSERKER) {
                return DRAGON_BERSERKER;
            } else if (role == Role.HEALER) {
                return DRAGON_HEALER;
            } else {
                return DRAGON_TANK;
            }
        } else {
            if (role == Role.BERSERKER) {
                return LION_BERSERKER;
            } else if (role == Role.HEALER) {
                return LION_HEALER;
            } else {
                return LION_TANK;
            }
        }
    }
}


