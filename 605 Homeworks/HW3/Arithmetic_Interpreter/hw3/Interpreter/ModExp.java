package Interpreter;

/**
 * The modulo expression class. Emits and evaluates a modular expression given
 * a left and right operand.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class ModExp implements Expression {

    // The left and right operands
    private final Expression left;
    private final Expression right;

    /**
     * Constructor initializes field.
     *
     * @param left left operand
     * @param right right operand
     */
    public ModExp(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Performs a modulo operation using the left and right operands.
     *
     * @return The integer result of the left operand mod the right operand.
     */
    @Override
    public int evaluate() {
        return left.evaluate() % right.evaluate();
    }

    /**
     * Creates an infix expression using the operator and the operands
     *
     * @return The string representation of the infix expression
     */
    @Override
    public String emit() {
        return "(" + left.emit() + " % " + right.emit() + ")";
    }
}
