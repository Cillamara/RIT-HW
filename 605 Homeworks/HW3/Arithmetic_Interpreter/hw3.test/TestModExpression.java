package test;

import Interpreter.ModExp;
import Interpreter.Expression;
import Interpreter.IntExp;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the ModExp class.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class TestModExpression {
    @Test
    public void testModExpressionInt() {
        Expression root = new ModExp(new IntExp(12), new IntExp(7));
        assertEquals(5, root.evaluate());
        assertEquals("(12 % 7)", root.emit());
    }

    @Test
    public void testModExpressionComplex() {
        Expression root = new ModExp(
                new ModExp(new IntExp(13), new IntExp(5)),
                new ModExp(new IntExp(4), new IntExp(6)));
        assertEquals(3, root.evaluate());
        assertEquals("((13 % 5) % (4 % 6))", root.emit());
    }
}
