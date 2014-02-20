package net.q3aiml.streampath.evaluator;

import net.q3aiml.streampath.ast.Expression;

/**
 * @author q3aiml
 */
public interface Context {
    /**
     * Returns the value of {@link expression} relative to {@param relativeFrame} based on the current context.
     */
    public <T> ContextValue<T> getValue(Frame relativeFrame, Expression<T, ?> expression);
}
