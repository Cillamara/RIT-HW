package Interpreter;

/**
 * The addition expression class. Emits and evaluates an addition expression
 * given a left and right operand.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class AddExp implements Expression {

    // The left and right operands
    private final Expression left;
    private final Expression right;

    /**
     * Constructor initializes field.
     *
     * @param left left operand
     * @param right right operand
     */
    public AddExp(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Sums the left and right operand
     *
     * @return the sum of the left and right operand
     */
    @Override
    public int evaluate() {
        return left.evaluate() + right.evaluate();
    }

    /**
     * Creates an infix expression using the operator and the operands
     *
     * @return The string representation of the infix expression
     */
    @Override
    public String emit() {
        return "(" + left.emit() + " + " + right.emit() + ")";
    }
}
