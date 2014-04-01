package net.q3aiml.streampath.ast.aggregate;

import net.q3aiml.streampath.ast.Expression;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author q3aiml
 */
public abstract class AggregatorNode<T> implements Expression<T> {
    public abstract Aggregator<T> aggregate();

    @Override
    public T apply(List<Object> arguments) {
        Aggregator<T> aggregate = aggregate();
        T value = aggregate.zero();
        for (Object argument : arguments) {
            value = aggregate.add(value, (T)argument);
        }
        return value;
    }

    @Override
    public String toVerboseString() {
        return toString();
    }
}
