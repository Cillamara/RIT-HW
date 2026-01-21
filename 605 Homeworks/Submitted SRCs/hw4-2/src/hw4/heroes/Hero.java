package hw4.heroes;

import hw4.game.Team;

import javax.sound.midi.Soundbank;

/**
 * Represents a virtuous hero in the venerable game of storms.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public abstract class Hero {

    //Hero name
    private final String name;
    //Hero max HP
    private final int maxHitPoints;
    //Hero current HP
    private int currentHitPoints;
    //Hero role
    private Heroes.Role role;
    //Hero team
    private Team team;
    //Hero party
    private Party party;

    /**
     * Constructor for a hero object. Sets the name and maximum HP of the hero.
     *
     * @param name Name of the hero
     * @param maxHitPoints Max HP of the hero
     */
    protected Hero(String name, int maxHitPoints) {
        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
    }

    /**
     * Factory method for creating specific classes of hero.
     * Sets the role and party affiliation of a hero
     * @param role Class role of a hero (Berserker, Healer, or Tank)
     * @param team Team of the hero (Dragon or Lion)
     * @param party Sets the party of the hero
     * @return
     */
    public static Hero create(Heroes.Role role, Team team, Party party) {
        if (role == Heroes.Role.BERSERKER) {
            return new Berserker(team);
        } else if (role == Heroes.Role.HEALER) {
            return new Healer(team, party);
        } else {
            return new Tank(team);
        }
    }

    /**
     * Getter for hero name
     *
     * @return Name of the hero
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for hero role
     *
     * @return Role of the hero
     */
    public Heroes.Role getRole() {
        return role;
    }

    /**
     * Setter for hero role
     *
     * @param role Role to set to
     */
    public void setRole(Heroes.Role role) {
        this.role = role;
    }

    /**
     * Getter for hero max HP
     *
     * @return Max HP of the hero
     */
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    /**
     * Heal action for each hero.
     * If hero health goes above max HP, set to max HP
     *
     * @param heal Amount to heal by
     */
    public void heal(int heal) {
        //If heal takes current HP over max, set to max
        System.out.println(this.getName() + " heals " + heal + " points");
        this.currentHitPoints = Math.min(this.currentHitPoints + heal,
                maxHitPoints);
    }

    /**
     * Method for hero taking damage. If hero is a tank, take 10% less damage
     * If HP goes below zero set HP to zero.
     *
     * @param damage Amount of damage to be taken
     */
    public void takeDamage(int damage) {
        if (this.role == Heroes.Role.TANK) {
            damage = (int) (damage * 0.9);
        }
        System.out.println(this.name + " takes " + damage + " damage");
        this.currentHitPoints = Math.max(this.currentHitPoints - damage, 0);
    }

    /**
     * Check if hero HP has fallen to 0 or less.
     *
     * @return True if HP is 0 or less, false otherwise
     */
    public boolean hasFallen() {
        if (currentHitPoints <=0)
            System.out.println(this.name + " has fallen" + "\n");
        return currentHitPoints <= 0;
    }

    /**
     * Abstract method for hero to be attack
     *
     * @param hero Hero to be attacked
     */
    public abstract void attack(Hero hero);

    /**
     * Displays hero name, role, and current hp out of max hp
     *
     * @return Hero name role and current HP
     */
    public String toString() {
        return name + ", " + role + ", " + currentHitPoints + "/" + maxHitPoints;
    }
}
