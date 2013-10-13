package net.q3aiml.streampath;

/**
 * @author q3aiml
 */
public class InvalidExpressionException extends StreamPathException {
    private final String expression;
    private final String errorDescription;

    public InvalidExpressionException(String expression, String errorDescription) {
        super("error parsing " + expression + ": " + errorDescription);
        this.expression = expression;
        this.errorDescription = errorDescription;
    }
}
