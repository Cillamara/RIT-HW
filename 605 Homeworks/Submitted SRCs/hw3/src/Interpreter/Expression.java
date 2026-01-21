package Interpreter;

/**
 * The Expression Interface. Evaluates mathematical expressions and generates
 * their infix expression
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public interface Expression {

    /**
     * Abstract method for evaluating math expression.
     *
     * @return The integer result of the expression
     */
    int evaluate();

    /**
     * Abstract method for generating the string representation of the infix
     * expression
     *
     * @return The infix expression in string form.
     */
    String emit();
}
