package net.q3aiml.streampath;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author q3aiml
 */
public class StaticExpressionTest extends ExpressionTestBase {
    @Test
    public void numericInequalityTest() throws IOException, InvalidExpressionException {
        assertEquals(false, eval("1 > 2"));
        assertEquals(false, eval("1 >= 2"));
        assertEquals(true, eval("1 < 2"));
        assertEquals(true, eval("1 <= 2"));

        assertEquals(true, eval("2 <= 2"));
        assertEquals(false, eval("3 <= 2"));
        assertEquals(true, eval("2 >= 2"));
        assertEquals(false, eval("2 >= 3"));

        assertEquals(true, eval("2 == 2"));
        assertEquals(true, eval("2 != 1"));
        assertEquals(false, eval("1 != 1"));
        assertEquals(false, eval("1 == 2"));
    }

    @Test
    public void stringEqualityTest() throws IOException, InvalidExpressionException {
        assertEquals(true, eval("\"a\" != \"b\""));
        assertEquals(false, eval("\"a\" != \"a\""));
    }

    @Test
    public void binaryBooleanLogicTest() throws IOException, InvalidExpressionException {
        assertEquals(true, eval("true and true"));
        assertEquals(false, eval("true and false"));
        assertEquals(false, eval("false and true"));
        assertEquals(false, eval("false and false"));

        assertEquals(true, eval("true or true"));
        assertEquals(true, eval("true or false"));
        assertEquals(true, eval("false or true"));
        assertEquals(false, eval("false or false"));

        assertEquals(true, eval("not false"));
        assertEquals(false, eval("not true"));
    }

    @Test
    public void arithmeticTest() throws IOException, InvalidExpressionException {
        assertEquals(new BigDecimal(2), eval("1 + 1"));
    }
}
