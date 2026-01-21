package test;

import Interpreter.DivExp;
import Interpreter.MulExp;
import Interpreter.SubExp;
import Interpreter.AddExp;
import Interpreter.ModExp;
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
public class TestComplexExpression {
    @Test
    public void testComplexExpression1() {
        Expression root = new MulExp(new IntExp(10), new IntExp(20));
        assertEquals(200, root.evaluate());
        assertEquals("(10 * 20)", root.emit());
    }

    @Test
    public void testComplexExpression2() {
        Expression root = new MulExp(
                new AddExp(new IntExp(1), new IntExp(2)),
                new AddExp(new IntExp(3), new IntExp(4)));
        assertEquals(21, root.evaluate());
        assertEquals("((1 + 2) * (3 + 4))", root.emit());
    }
}
