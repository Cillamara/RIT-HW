package Interpreter;

import java.util.Scanner;

/**
 * The Interp class. Prompts a user to input a prefix expression and the
 * program emits the infix notation followed by the integer evaluation of
 * the expression.
 *
 * @author Liam Cui
 * @author Tyler Ronnenberg
 */
public class Interp {

    /**
     * A helper function. Recursively generates an expression tree by parsing
     * the prefix token array. It reads the token array incrementally. If the
     * token is an operator, it then calls the dedicated class and recursively
     * calls the method twice for the left and right parameters. Otherwise, the
     * integer values are stored in an intExp class.
     *
     * @param tokens The input converted into a string array of "tokens".
     * @param index A counter which relates to the position on the tokens array
     * @return the expression class object
     */
    private static Expression prefixToken(String[] tokens, int[] index) {

        // Go forward in the token array
        String token = tokens[index[0]++];
        // if token is an operator, recursively builds tree
        if (token.equals("+")) {
            return new AddExp(prefixToken(tokens, index), prefixToken(tokens,
                    index));
        } else if (token.equals("-")) {
            return new SubExp(prefixToken(tokens, index), prefixToken(tokens,
                    index));
        } else if (token.equals("*")) {
            return new MulExp(prefixToken(tokens, index), prefixToken(tokens,
                    index));
        } else if (token.equals("/")) {
            return new DivExp(prefixToken(tokens, index), prefixToken(tokens,
                    index));
        } else if (token.equals("%")) {
            return new ModExp(prefixToken(tokens, index), prefixToken(tokens,
                    index));
        } else {
            //values stored in a new IntExp class
            int value = Integer.parseInt(token);
            return new IntExp(value);
        }
    }

    /**
     * The main method. A loop which prompts user to input a prefix expression.
     * Then converts the expression into a token array and passes that into the
     * prefixToken method which emits the infix expression and evaluates it. An
     * input of "quit" breaks the loop and the program ends.
     */
    public static void main(String[] args) {

        Boolean sugma = true;
        while (sugma) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Welcome to your Arithmetic Interpreter v1.0 :)\n"
                    + "> ");
            String input = scanner.nextLine();

            // quit conditional
            if (input.equals("quit"))
                break;

            // Convert input expression as an array of tokens
            String[] tokens = input.split(" ");

            // initializing a counter for the position of the tokens array
            int[] index = {0};

            // initializes the prefixToken method with above parameters
            Expression expression = prefixToken(tokens, index);
            System.out.println("Emit: " + expression.emit());
            System.out.println("Evaluate: " + expression.evaluate());
        }
        // quit conditional is true
        System.out.println("Goodbye!");
    }
}


