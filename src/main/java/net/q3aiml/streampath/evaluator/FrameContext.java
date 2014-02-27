package net.q3aiml.streampath.evaluator;

import net.q3aiml.streampath.ast.Expression;

/**
 * @author q3aiml
 */
/*package*/ class FrameContext implements Context {
    private final Evaluator evaluator;

    public FrameContext(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public <T> ContextValue<T> getValue(Frame relativeFrame, Expression<T> expression) {
        Object value = evaluator.evaluate(relativeFrame, expression, null);
        if (value instanceof ContextValue) {
            return (ContextValue<T>)value;
        } else {
            return ContextValue.available((T)value);
        }
    }
}
