package net.q3aiml.streampath;

/**
 * Thrown when an invalid expression is provided for evaluation.
 * @author q3aiml
 */
public class InvalidExpressionException extends StreamPathException {
    private final String expression;
    private final String errorDescription;
    private final Integer startIndex;
    private final Integer endIndex;

    public InvalidExpressionException(String expression, String errorDescription, Integer startIndex, Integer endIndex) {
        super("error parsing " + expression + ": " + errorDescription);
        this.expression = expression;
        this.errorDescription = errorDescription;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public InvalidExpressionException(String expression, String errorDescription) {
        this(expression, errorDescription, null, null);
    }

    public String expression() {
        return expression;
    }

    public String errorDescription() {
        return errorDescription;
    }

    public Integer startIndex() {
        return startIndex;
    }

    public Integer endIndex() {
        return endIndex;
    }
}
