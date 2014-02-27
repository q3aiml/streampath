package net.q3aiml.streampath.lang;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.DocumentSets;
import net.q3aiml.streampath.StreamPath;
import net.q3aiml.streampath.StreamPathException;
import net.q3aiml.streampath.StreamPathResult;
import net.q3aiml.streampath.ast.Expression;
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
        ParsingResult<Expression<?>> result = new ReportingParseRunner(parser.Expression()).run(input);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        if (!result.hasErrors()) {
            System.out.println(parseTreePrintOut);
            StreamPathResult value = null;
            try {
                value = new StreamPath().evaluate(DocumentSets.empty(), ImmutableSet.of(input));
            } catch (StreamPathException e) {
                e.printStackTrace();
            }
            System.out.println("value! " + input + " -> " + value);
            System.out.println();
        } else {
            System.out.println(input);
            System.out.println("parse errors:");
            System.out.println(ErrorUtils.printParseErrors(result.parseErrors));
            System.out.println();
        }
    }

}
