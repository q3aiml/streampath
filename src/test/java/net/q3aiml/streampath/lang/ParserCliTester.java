package net.q3aiml.streampath.lang;

import com.google.common.base.Strings;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.evaluator.Frame;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author q3aiml
 */
public class ParserCliTester {
    private static Parser parser = Parboiled.createParser(Parser.class);

    public static void main(String... args) throws IOException {
        new ParserCliTester().run();
    }

    public void run() throws IOException {
        while (true) {
            String input = new Scanner(System.in).nextLine();
            if (Strings.isNullOrEmpty(input)) break;

            parseAndPrint(input);
        }
    }

    public void parseAndPrint(String input) throws IOException {
        ParsingResult<Expression<?, ?>> result = new ReportingParseRunner(parser.Expression()).run(input);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        if (!result.hasErrors()) {
            System.out.println(parseTreePrintOut);
            System.out.println("value! " + input + " -> " + result.parseTreeRoot.getValue().getValue(new Context() {
                @Override
                public Frame frame() {
                    return null;
                }
            }));
            System.out.println();
        } else {
            System.out.println(input);
            System.out.println("parse errors:");
            System.out.println(ErrorUtils.printParseErrors(result.parseErrors));
            System.out.println();
        }
    }

}
