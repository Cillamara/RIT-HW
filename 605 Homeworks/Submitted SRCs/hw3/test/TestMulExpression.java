package test;

import Interpreter.MulExp;
import Interpreter.Expression;
import Interpreter.IntExp;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the MulExp class.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class TestMulExpression {
    @Test
    public void testMulExpressionInt() {
        Expression root = new MulExp(new IntExp(1), new IntExp(2));
        assertEquals(2, root.evaluate());
        assertEquals("(1 * 2)", root.emit());
    }

    @Test
    public void testMulExpressionComplex() {
        Expression root = new MulExp(
                new MulExp(new IntExp(1), new IntExp(2)),
                new MulExp(new IntExp(3), new IntExp(4)));
        assertEquals(24, root.evaluate());
        assertEquals("((1 * 2) * (3 * 4))", root.emit());
    }
}
