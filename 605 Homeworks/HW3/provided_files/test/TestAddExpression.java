package hw3.test;

import hw3.AddExpression;
import hw3.Expression;
import hw3.IntExpression;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;

/**
 * A test unit for the AddExpression class.
 *
 * @author RIT CS
 */
public class TestAddExpression {
    @Test
    public void testAddExpressionInt() {
        Expression root = new AddExpression(new IntExpression(10), new IntExpression(20));
        assertEquals(30, root.evaluate());
        assertEquals("(10 + 20)", root.emit());
    }

    @Test
    public void testAddExpressionComplex() {
        Expression root = new AddExpression(
                new AddExpression(new IntExpression(10), new IntExpression(20)),
                new AddExpression(new IntExpression(30), new IntExpression(40)));
        assertEquals(100, root.evaluate());
        assertEquals("((10 + 20) + (30 + 40))", root.emit());
    }
}
