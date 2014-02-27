package net.q3aiml.streampath.ast.aggregate;

import com.google.common.base.Joiner;
import net.q3aiml.streampath.ast.Expression;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author q3aiml
 */
/*package*/ abstract class NodeSetAggregateFunction implements AggregatorNode<BigDecimal, Object> {
    private final List<Expression<?>> arguments;

    protected NodeSetAggregateFunction(List<Expression<?>> arguments) {
        this.arguments = arguments;
    }

    public static NodeSetAggregateFunction count(List<Expression<?>> arguments) {
        return new Count(arguments);
    }

    @Override
    public List<? extends Expression> children() {
        return arguments;
    }

    @Override
    public Class<BigDecimal> getValueType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal apply(List<Object> arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    public abstract Aggregator<BigDecimal> aggregate();

    @Override
    public String toString() {
        return getClass().getSimpleName().toLowerCase() + "(" + Joiner.on(", ").join(arguments) + ")";
    }

    /*package*/ static class Count extends NodeSetAggregateFunction {
        public Count(List<Expression<?>> operands) {
            super(operands);
        }

        @Override
        public Aggregator<BigDecimal> aggregate() {
            return MyAggregates.COUNT;
        }

        @Override
        public boolean isConstant() {
            return false;
        }
    }

    private static enum MyAggregates implements Aggregator<BigDecimal> {
        COUNT() {
            @Override
            public BigDecimal map(Object a) {
                return BigDecimal.ONE;
            }

            @Override
            public BigDecimal add(BigDecimal a, BigDecimal b) {
                return a.add(b);
            }

            @Override
            public BigDecimal zero() {
                return BigDecimal.ZERO;
            }
        }
    }
}
