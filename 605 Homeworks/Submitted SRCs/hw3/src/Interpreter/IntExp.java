package Interpreter;

/**
 * Receives an integer token and stores it as a literal with the evaluate
 * method and as a string with the emit method.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class IntExp implements Expression {

    // token as an integer
    private final int value;

    /**
     * Constructor initializes field.
     *
     * @param value an integer
     */
    public IntExp(int value) {
        this.value = value;
    }

    /**
     * Stores the literal integer value
     *
     * @return the integer
     */
    @Override
    public int evaluate() {
        return value;
    }

    /**
     * Stores the integer value as a string
     *
     * @return The string
     */
    @Override
    public String emit() {
        return Integer.toString(value);
    }
}
