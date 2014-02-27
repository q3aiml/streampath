package net.q3aiml.streampath.ast.aggregate;

import net.q3aiml.streampath.ast.Expression;

import java.math.BigDecimal;

/**
 * @author q3aiml
 */
public interface AggregatorNode<T, I> extends Expression<T> {
    public Aggregator<BigDecimal> aggregate();
}
