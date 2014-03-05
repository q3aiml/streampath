package net.q3aiml.streampath;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.Source;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author q3aiml
 */
public class DocumentExpressionTest extends ExpressionTestBase {
    private static Source doc() throws IOException {
        return doc(TestDocuments.read("doc.xml"));
    }

    private static Source attrDoc() {
        return doc(
              "<root>"
            + " <number type='a'>3</number>"
            + " <number type='b'>7</number>"
            + " <number type='b'>11</number>"
            + "</root>"
        );
    }

    @Test
    public void aggregateFunctionTest() throws IOException, StreamPathException {
        assertEquals(new BigDecimal(48), eval("sum(//value)", doc()));
        assertEquals(new BigDecimal(48), eval("sum(/root/a/item/value)", doc()));
        assertEquals(new BigDecimal(48), eval("sum(/root/a//value)", doc()));
        assertEquals(new BigDecimal(48), eval("sum(/root//item/value)", doc()));

        assertEquals(new BigDecimal(3), eval("min(/root/a/item/value)", doc()));
        assertEquals(new BigDecimal(22), eval("max(/root/a/item/value)", doc()));
    }

    @Test
    public void selectorAttributePredicateTest() throws IOException, StreamPathException {
        assertEquals(new BigDecimal(2), eval("count(/root/number[@type == \"b\"])", attrDoc()));
        assertEquals(new BigDecimal(1), eval("count(//number[@type == \"a\"])", attrDoc()));
        assertEquals(new BigDecimal(18), eval("sum(//number[@type == \"b\"])", attrDoc()));
    }

    @Test
    public void selectorSiblingPredicateTest() throws IOException, StreamPathException {
        assertEquals(new BigDecimal(10), eval("sum(/root/a/item/value[../type == \"RED\"])", doc()));
        assertEquals(new BigDecimal(16), eval("sum(/root/a/item/value[../type == \"BLACK\"])", doc()));
    }

    @Test
    public void selectSimpleValueTest() throws IOException, StreamPathException {
        assertEquals("22", eval("/root/a/item/value[../type == \"SPECIAL\"]", doc()));
    }

    @Test
    public void selectSimpleValueAndAggregateTest() throws IOException, StreamPathException {
        assertEquals(true, eval("/root/a/item/value[../type == \"SPECIAL\"] == sum(/root/a/item/value[../type == \"RED\"]) + 12" , doc()));
    }
}
