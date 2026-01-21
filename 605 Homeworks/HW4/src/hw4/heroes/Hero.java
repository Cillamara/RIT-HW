package hw4.heroes;

import hw4.game.Team;

public abstract class Hero {
    private final String name;
    private final int maxHitPoints;
    private int currentHitPoints;
    private Heroes.Role role;
    private Team team;
    private Party party;

    protected Hero(String name, int maxHitPoints) {
        this.name = name;
        this.maxHitPoints = maxHitPoints;
        this.currentHitPoints = maxHitPoints;
    }

    public static Hero create(Heroes.Role role, Team team, Party party) {
        if (role == Heroes.Role.BERSERKER) {
            return new Berserker(team);
        } else if (role == Heroes.Role.HEALER) {
            return new Healer(team, party);
        } else {
            return new Tank(team);
        }
    }

    public String getName() {
        return name;
    }

    public Heroes.Role getRole() {
        return role;
    }

    public void setRole(Heroes.Role role) {
        this.role = role;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public void heal(int heal) {
        //If heal takes current HP over max, set to max
        this.currentHitPoints = Math.min(this.currentHitPoints + heal,
                maxHitPoints);
    }

    public void takeDamage(int damage) {
        if (this.role == Heroes.Role.TANK) {
            damage = (int) (damage * 0.9);
        }
        System.out.println(this.name + " takes " + damage + " damage");
        this.currentHitPoints = Math.max(this.currentHitPoints - damage, 0);
    }

    public boolean hasFallen() {
        if (currentHitPoints <=0)
            System.out.println(this.name + " has fallen");
        return currentHitPoints <= 0;
    }

    public abstract void attack(Hero hero);

    public String toString() {
        return name + ", " + role + ", " + currentHitPoints + "/" + maxHitPoints;
    }
}