package net.q3aiml.streampath;

import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author q3aiml
 */
public class DocumentExpressionTest extends ExpressionTestBase {
    private static Source doc() {
        return doc(
              "<root>\n"
            + "    <a>\n"
            + "        <item>\n"
            + "            <type>RED</type>\n"
            + "            <value>3</value>\n"
            + "        </item>\n"
            + "        <item>\n"
            + "            <type>BLACK</type>\n"
            + "            <value>5</value>\n"
            + "        </item>\n"
            + "        <item>\n"
            + "            <type>RED</type>\n"
            + "            <value>7</value>\n"
            + "        </item>\n"
            + "        <item>\n"
            + "            <type>BLACK</type>\n"
            + "            <value>11</value>\n"
            + "        </item>\n"
            + "    </a>"
            + "</root>"
        );
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

    private static Source doc(String contents) {
        return new StreamSource(new StringReader(contents));
    }

    @Test
    public void aggregateFunctionTest() throws IOException, InvalidExpressionException {
        assertEquals(new BigDecimal(26), eval("sum(//value)", doc()));
        assertEquals(new BigDecimal(26), eval("sum(/root/a/item/value)", doc()));
        assertEquals(new BigDecimal(26), eval("sum(/root/a//value)", doc()));
        assertEquals(new BigDecimal(26), eval("sum(/root//item/value)", doc()));

        assertEquals(new BigDecimal(3), eval("min(/root/a/item/value)", doc()));
        assertEquals(new BigDecimal(11), eval("max(/root/a/item/value)", doc()));
    }

    @Test
    public void selectorAttributePredicateTest() throws IOException, InvalidExpressionException {
        assertEquals(new BigDecimal(2), eval("count(/root/number[@type == \"b\"])", attrDoc()));
        assertEquals(new BigDecimal(1), eval("count(//number[@type == \"a\"])", attrDoc()));
        assertEquals(new BigDecimal(18), eval("sum(//number[@type == \"b\"])", attrDoc()));
    }
}
