package test;

import Interpreter.Expression;
import Interpreter.IntExp;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the IntExp class.
 *
 * @author RIT CS
 */
public class TestIntExpression {
    @Test
    public void testIntExpression() {
        Expression root = new IntExp(10);
        assertEquals(10, root.evaluate());
        assertEquals("10", root.emit());
    }
}
