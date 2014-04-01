package net.q3aiml.streampath;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.*;
import net.q3aiml.streampath.ast.Expression;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ajclayton
 */
public class StreamPath {
    private static final Logger log = LoggerFactory.getLogger(StreamPath.class);

    private final boolean verbose;
    private final Parser parser;

    public StreamPath() {
        verbose = false;
        parser = Parboiled.createParser(Parser.class);
    }

    public StreamPath(StreamPathConfiguration configuration) {
        this.verbose = configuration.isVerbose();
        parser = Parboiled.createParser(Parser.class, configuration);
    }

    @VisibleForTesting
    protected Parser parser() {
        return parser;
    }

    @SuppressWarnings("DuplicateThrows")
    public StreamPathResult evaluateStrings(DocumentSet documentSet, Set<String> expressions)
            throws IOException, InvalidExpressionException, InvalidDocumentException, StreamPathException
    {
        return evaluate(documentSet, compile(expressions));
    }

    @SuppressWarnings("DuplicateThrows")
    public StreamPathResult evaluate(DocumentSet documentSet, Set<StreamPathExpression> compiledExpressions)
            throws IOException, InvalidExpressionException, InvalidDocumentException, StreamPathException
    {
        ImmutableSet.Builder<Expression<?>> internalExpressionsBuilder = ImmutableSet.builder();
        ImmutableBiMap.Builder<String, Expression<?>> expressionsMap = ImmutableBiMap.builder();
        for (StreamPathExpression expression : compiledExpressions) {
            internalExpressionsBuilder.add(expression.expression());
            expressionsMap.put(expression.originalExpression(), expression.expression());
        }

        ImmutableSet<Expression<?>> internalExpressions = internalExpressionsBuilder.build();
        Evaluator evaluator = new Evaluator(documentSet, internalExpressions);
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
            Map<Expression<?>, String> originalExpressionMap = new HashMap<Expression<?>, String>();
            for (StreamPathExpression expression : compiledExpressions) {
                originalExpressionMap.putAll(expression.originalExpressionMap());
            }

            for (Expression<?> expression : result.results().keySet()) {
                if (!internalExpressions.contains(expression)) {
                    String originalExpression = originalExpressionMap.get(expression);
                    checkNotNull(originalExpression, "unable to find original expression for %s", expression);
                    expressionsMap.put(expression.toString(), expression);
                }
            }
        }

        final BiMap<String, Expression<?>> finalExpressionsMap = expressionsMap.build();
        return new StreamPathResult() {
            public Object result(String expression) {
                Expression<?> compiledExpression = finalExpressionsMap.get(expression);
                checkArgument(compiledExpression != null, "expression is not in results: " + expression);
                return result.result(compiledExpression);
            }

            public Set<String> expressions() {
                return finalExpressionsMap.keySet();
            }
        };
    }

    public Set<StreamPathExpression> compile(Set<String> expressions)
            throws InvalidExpressionException
    {
        ImmutableSet.Builder<StreamPathExpression> compiledExpressions = ImmutableSet.builder();
        for (String expression : expressions) {
            compiledExpressions.add(compile(expression));
        }
        return compiledExpressions.build();
    }

    public StreamPathExpression compile(String expression) throws InvalidExpressionException {
        @SuppressWarnings("unchecked")
        ParsingResult<Expression<?>> result = new ReportingParseRunner(parser.Expression()).run(expression);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        if (!result.hasErrors()) {
            log.debug("parse tree for " + expression + ":\n" + parseTreePrintOut);
            return new StreamPathExpression(expression, result);
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
