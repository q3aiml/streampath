package net.q3aiml.streampath;

import com.google.common.collect.ImmutableSet;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author q3aiml
 */
public abstract class ExpressionTestBase {
    public Object eval(String expression) throws IOException, StreamPathException {
        return eval(expression, (Source)null);
    }

    public Object eval(String expression, Source document) throws IOException, StreamPathException {
        return eval(expression, document != null ? DocumentSets.ofSource(document) : DocumentSets.empty());
    }

    public Object eval(String expression, DocumentSet documentSet) throws StreamPathException, IOException {
        StreamPathResult results = new StreamPath()
                .compileAndEvaluate(documentSet, ImmutableSet.of(expression));
        final Object result = results.result(expression);
        System.out.println("evaluator value! " + expression + " -> " + result);
        System.out.println();

        return result;
    }

    public static Source doc(String contents) {
        return new StreamSource(new StringReader(contents));
    }
}
