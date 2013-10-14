package net.q3aiml.streampath.evaluator;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.streampath.ast.Expression;

import java.util.Map;

/**
* @author q3aiml
*/
public class EvaluationResult {
    private final ImmutableMap<Expression<?, ?>, Object> expressionResults;

    public EvaluationResult(Map<Expression<?, ?>, Object> expressionResults) {
        this.expressionResults = ImmutableMap.copyOf(expressionResults);
    }

    public ImmutableMap<Expression<?, ?>, Object> results() {
        return expressionResults;
    }

    public <T> T result(Expression<T, ?> expression) {
        final Object result = expressionResults.get(expression);
        if (result == null) {
            throw new IllegalArgumentException("no such expression in results: " + expression);
        }
        //noinspection unchecked
        return (T)result;
    }
}
