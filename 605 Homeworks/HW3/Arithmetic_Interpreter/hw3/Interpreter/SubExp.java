package Interpreter;

/**
 * The subtraction expression class. Emits and evaluates a subtraction
 * expression given a left and right operand.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class SubExp implements Expression {

    // The left and right operands
    private final Expression left;
    private final Expression right;

    /**
     * Constructor initializes field.
     *
     * @param left left operand
     * @param right right operand
     */
    public SubExp(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Subtracts the right operand from the left.
     *
     * @return the difference integer value
     */
    @Override
    public int evaluate() {
        return left.evaluate() - right.evaluate();
    }

    /**
     * Creates an infix expression using the operator and the operands
     *
     * @return The string representation of the infix expression
     */
    @Override
    public String emit() {
        return "(" + left.emit() + " - " + right.emit() + ")";
    }
}
