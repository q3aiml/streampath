package net.q3aiml.streampath;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author ajclayton
 */
public abstract class StreamPathResult {
    /**
     * Returns the result of {@code expression}, never {@code null}.
     * @throws IllegalArgumentException if {@code expression} is not part of the results.
     */
    public abstract Object result(String expression);

    public abstract Set<String> expressions();

    public String resultAsString(String expression) {
        Object result = result(expression);
        return Objects.toString(result);
    }

    /**
     * @throws java.lang.ArithmeticException if the result of {@param expression} can not be represented as an int
     */
    public int resultAsInt(String expression) {
        Object result = result(expression);
        if (result instanceof BigDecimal) {
            return ((BigDecimal)result).intValueExact();
        }
        String resultAsString = Objects.toString(result);
        try {
            return Integer.parseInt(resultAsString);
        } catch (NumberFormatException e) {
            throw new ArithmeticException(resultAsString);
        }
    }

    public Map<String, Object> results() {
        ImmutableMap.Builder<String, Object> results = ImmutableMap.builder();
        for (String expression : expressions()) {
            results.put(expression, result(expression));
        }
        return results.build();
    }
}
