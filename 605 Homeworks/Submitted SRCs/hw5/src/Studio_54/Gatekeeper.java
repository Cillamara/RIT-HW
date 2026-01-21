package Studio_54;

import java.util.Objects;
import java.util.Scanner;

/**
 * Class for simulating the Studio54 admission line
 * using the HeapQueue data structure
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class Gatekeeper {
    /**
     * HeapQueue representing the line of patrons
     */
     PriorityQueue<Patron> patrons;

    /**
     * Initializes data structure depending on mode selection
     */
    public Gatekeeper(String mode) {
        if (Objects.equals(mode, "heap")) {
            patrons = new HeapQueue<>();
        } else if (Objects.equals(mode, "linked")) {
            patrons = new LinkedQueue<>();
        }
    }

    /**
     * Method for driving the simulation.
     * Offers 3 options:
     *     1) Add a member to the HeapQueue and sort based on priority
     *     2) Pop the highest priority member of the HeapQueue
     *     3) Quit the program
     */
    public static void Studio54(String mode) {
        Gatekeeper gatekeeper = new Gatekeeper(mode);
        String option;
        do {
            System.out.println("""
                    Enter an option
                    1 to Add a patron to the queue
                    2 to Admit a patron
                    3 to Close for the night (quit)""");
            System.out.print("Your choice: ");
            Scanner scanner = new Scanner(System.in);
            option = scanner.next();
            switch (option) {
                case "1":
                    Scanner in = new Scanner(System.in);
                    System.out.println("Enter the patron name: ");
                    String name = in.nextLine();
                    System.out.println("Coolness (1-10): ");
                    String coolness = in.next();
                    while (!coolness.matches("[1-9]|10")) {
                        System.out.print("Invalid input. " +
                                "Please enter value between 1 and 10: ");
                        coolness = in.next();
                    }
                    System.out.println("Regular (y/n): ");
                    String regular = in.next();
                    while (!regular.equals("y") && !regular.equals("n")) {
                        System.out.print("Invalid input. Please enter y/n: ");
                        regular = in.next();
                    }
                    Patron patron;
                    if (regular.equals("y")) {
                        patron = new Patron(name, Integer.parseInt(coolness), true);
                    } else {
                        patron = new Patron(name, Integer.parseInt(coolness), false);
                    }
                    gatekeeper.patrons.enqueue(patron);
                    break;
                case "2":
                    if (gatekeeper.patrons.isEmpty()) {
                        System.out.println("There are no patrons in the queue");
                    } else {
                        System.out.println(gatekeeper.patrons.dequeue().toString()
                                + " gets in!!!");
                    }
                    break;
                case "3":
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        } while (!option.equals("3"));
    }

    public static void main(String[] args) {

        System.out.println("""
                    Enter data structure for patrons
                    "heap" - store patron data with a HeapQueue
                    "linked" - store patron data with a LinkedQueue""");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        while (!option.equals("heap") && !option.equals("linked")) {
            System.out.println("Invalid input. Please enter 'heap' or 'linked' : ");
            option = scanner.nextLine();
        }
        Studio54(option);
    }

}
