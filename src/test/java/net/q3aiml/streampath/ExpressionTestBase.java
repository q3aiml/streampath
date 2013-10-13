package net.q3aiml.streampath;

import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.evaluator.Frame;
import net.q3aiml.streampath.lang.Parser;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import javax.xml.transform.Source;
import java.io.IOException;

/**
 * @author q3aiml
 */
public abstract class ExpressionTestBase {
    private static Parser parser = Parboiled.createParser(Parser.class);

    public Object eval(String expression) throws IOException, InvalidExpressionException {
        return eval(expression, new Source[0]);
    }

    public Object eval(String expression, Source... documents) throws IOException, InvalidExpressionException {
        ParsingResult<Expression> parseResult = new ReportingParseRunner(parser.Expression()).run(expression);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(parseResult);
        if (!parseResult.hasErrors()) {
            System.out.println(parseTreePrintOut);

            final Object result = parseResult.parseTreeRoot.getValue().getValue(new Context() {
                @Override
                public Frame frame() {
                    return null;
                }
            });
            System.out.println("evaluator value! " + expression + " -> " + result);

            System.out.println();

            return result;
        } else {
            throw new InvalidExpressionException(expression, ErrorUtils.printParseErrors(parseResult.parseErrors));
        }
    }
}
