package net.q3aiml.streampath;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.FunctionFactory;
import net.q3aiml.streampath.evaluator.EvaluationResult;
import net.q3aiml.streampath.evaluator.Evaluator;
import net.q3aiml.streampath.lang.Parser;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author ajclayton
 */
public class StreamPath {
    private static final Logger log = LoggerFactory.getLogger(StreamPath.class);

    private final Parser parser;

    public StreamPath() {
        parser = Parboiled.createParser(Parser.class);
    }

    public StreamPath(Map<String, FunctionFactory> functions) {
        parser = Parboiled.createParser(Parser.class, functions);
    }

    @VisibleForTesting
    protected Parser parser() {
        return parser;
    }

    @SuppressWarnings("DuplicateThrows")
    public StreamPathResult evaluate(DocumentSet documentSet, final Set<String> expressions)
            throws IOException, InvalidExpressionException, InvalidDocumentException, StreamPathException
    {
        return evaluate(documentSet, expressions, false);
    }

    @SuppressWarnings("DuplicateThrows")
    public StreamPathResult evaluate(DocumentSet documentSet, Set<String> expressions, boolean verbose)
            throws IOException, InvalidExpressionException, InvalidDocumentException, StreamPathException
    {
        BiMap<String, Expression<?>> compiledExpressions = compile(expressions);
        Evaluator evaluator = new Evaluator(documentSet, compiledExpressions.values());
        final EvaluationResult result;
        try {
            result = evaluator.evaluate(verbose);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof StreamPathException) {
                throw (StreamPathException)cause;
            }
            throw e;
        }

        if (verbose) {
            expressions = new HashSet<String>(expressions);
            compiledExpressions = HashBiMap.create(compiledExpressions);
            for (Expression<?> expression : result.results().keySet()) {
                if (!compiledExpressions.containsValue(expression)) {
                    expressions.add(expression.toString());
                    compiledExpressions.put(expression.toString(), expression);
                }
            }
            expressions = ImmutableSet.copyOf(expressions);
        }

        final Set<String> finalExpressions = expressions;
        final BiMap<String, Expression<?>> finalCompiledExpressions = compiledExpressions;
        return new StreamPathResult() {
            public Object result(String expression) {
                Expression<?> compiledExpression = finalCompiledExpressions.get(expression);
                checkArgument(compiledExpression != null, "expression is not in results: " + expression);
                return result.result(compiledExpression);
            }

            public Set<String> expressions() {
                return finalExpressions;
            }
        };
    }

    public ImmutableBiMap<String, Expression<?>> compile(Set<String> expressions)
            throws InvalidExpressionException
    {
        ImmutableBiMap.Builder<String, Expression<?>> compiledExpressions = ImmutableBiMap.builder();
        for (String expression : expressions) {
            compiledExpressions.put(expression, compile(expression));
        }
        return compiledExpressions.build();
    }

    public Expression<?> compile(String expression) throws InvalidExpressionException {
        @SuppressWarnings("unchecked")
        ParsingResult<Expression<?>> result = new ReportingParseRunner(parser.Expression()).run(expression);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        if (!result.hasErrors()) {
            log.debug("parse tree for " + expression + ":\n" + parseTreePrintOut);
            return result.parseTreeRoot.getValue();
        } else {
            Integer startIndex = null;
            Integer endIndex = null;
            if (result.parseErrors.size() > 0) {
                ParseError parseError = result.parseErrors.get(0);
                startIndex = parseError.getStartIndex();
                endIndex = parseError.getEndIndex();
            }
            throw new InvalidExpressionException(expression,
                    ErrorUtils.printParseErrors(result.parseErrors), startIndex, endIndex);
        }
    }
}
