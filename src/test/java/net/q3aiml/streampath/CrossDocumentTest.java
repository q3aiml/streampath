package net.q3aiml.streampath;

import org.junit.Test;

import javax.xml.transform.Source;
import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author ajclayton
 */
public class CrossDocumentTest extends ExpressionTestBase {
    private static Source doc1() {
        return doc(
              "<root>\n"
            + "    <valuea>30</valuea>\n"
            + "    <valuea>50</valuea>\n"
            + "    <valueb>70</valueb>\n"
            + "    <valueb>110</valueb>\n"
            + "</root>"
        );
    }
    private static Source doc2() {
        return doc(
              "<root>\n"
            + "    <valuea>3</valuea>\n"
            + "    <valuea>5</valuea>\n"
            + "    <valueb>7</valueb>\n"
            + "    <valueb>11</valueb>\n"
            + "</root>"
        );
    }

    public DocumentSet testDocumentSet() {
        return DocumentSets.create()
                .add(null, doc1())
                .add("other", doc2())
                .build();
    }

    @Test
    public void separateAggregationsTest() throws IOException, StreamPathException {
        assertEquals(new BigDecimal(98), eval("sum(//valuea) + sum(document(\"other\"):://valueb)", testDocumentSet()));
    }

}
