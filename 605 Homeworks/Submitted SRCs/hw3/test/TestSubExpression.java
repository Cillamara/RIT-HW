package test;

import Interpreter.SubExp;
import Interpreter.Expression;
import Interpreter.IntExp;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the SubExp class.
 *
 * @author Tyler Ronnenberg
 * @author Liam Cui
 */
public class TestSubExpression {

    @Test
    public void testAddExpressionInt() {
        Expression root = new SubExp(new IntExp(10), new IntExp(20));
        assertEquals(-10, root.evaluate());
        assertEquals("(10 - 20)", root.emit());
    }

    @Test
    public void testAddExpressionComplex() {
        Expression root = new SubExp(
                new SubExp(new IntExp(10), new IntExp(20)),
                new SubExp(new IntExp(30), new IntExp(40)));
        assertEquals(0, root.evaluate());
        assertEquals("((10 - 20) - (30 - 40))", root.emit());
    }
}
