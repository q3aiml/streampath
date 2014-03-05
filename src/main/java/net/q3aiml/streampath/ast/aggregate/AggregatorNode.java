package net.q3aiml.streampath.ast.aggregate;

import net.q3aiml.streampath.ast.Expression;

import java.math.BigDecimal;

/**
 * @author q3aiml
 */
public abstract class AggregatorNode<T> implements Expression<T> {
    public abstract Aggregator<T> aggregate();
}
