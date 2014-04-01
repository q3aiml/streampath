package net.q3aiml.streampath.ast;

import java.util.List;

/**
 * @author q3aiml
 */
public interface Expression<T> extends StreamPathNode {
    public Expression<T> copy(List<Expression<?>> children);

    public List<? extends Expression> children();

    public Class<T> getValueType();

    public T apply(List<Object> arguments);

    public boolean isConstant();

    /**
     * Returns a verbose, non-parsable representation of this expression.
     * <p/>
     * This representation of the expression contains additional information that is implied, inferred, or otherwise
     * omitted from the parsable representation.
     */
    public String toVerboseString();

    /**
     * A parsable representation of this expression that, when parsed, yields the same expression.
     * <p/>
     * This string is only guaranteed to parse to the same expression and not necessarily be the
     * string that was parsed to yield this expression.
     */
    public String toString();
}
