package Studio_54;

import java.util.Scanner;

public class Gatekeeper {

    HeapQueue<Patron> patrons;

    public Gatekeeper() {
        patrons = new HeapQueue<>();
    }

    public static void Studio54() {
        Gatekeeper gatekeeper = new Gatekeeper();
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
                        Patron admittedPatron = gatekeeper.patrons.dequeue();
                        System.out.println(admittedPatron.toString()
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
        Studio54();
    }

}
