package test;

import Interpreter.DivExp;
import Interpreter.Expression;
import Interpreter.IntExp;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the DivExp class.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class TestDivExpression {
    @Test
    public void testDivExpressionInt() {
        Expression root = new DivExp(new IntExp(4), new IntExp(2));
        assertEquals(2, root.evaluate());
        assertEquals("(4 / 2)", root.emit());
    }

    @Test
    public void testDivExpressionComplex() {
        Expression root = new DivExp(
                new DivExp(new IntExp(100), new IntExp(25)),
                new DivExp(new IntExp(40), new IntExp(20)));
        assertEquals(2, root.evaluate());
        assertEquals("((100 / 25) / (40 / 20))", root.emit());
    }
}
