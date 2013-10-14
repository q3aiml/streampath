package net.q3aiml.streampath.ast.aggregate;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Expressions;
import net.q3aiml.streampath.ast.cast.ImplicitCast;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author q3aiml
 */
/*package*/ abstract class NumericAggregateFunction implements AggregatorNode<BigDecimal, Object> {
    protected final List<Expression<BigDecimal, ?>> arguments;

    protected NumericAggregateFunction(List<Expression<?, ?>> arguments) {
        this.arguments = ImmutableList.copyOf(Lists.transform(arguments,
                new Function<Expression, Expression<BigDecimal, ?>>() {
                    @Override
                    public Expression<BigDecimal, ?> apply(Expression input) {
                        return ImplicitCast.numeric(input);
                    }
                })
        );
    }

    public static NumericAggregateFunction min(List<Expression<?, ?>> arguments) {
        return new Min(arguments);
    }

    public static NumericAggregateFunction max(List<Expression<?, ?>> arguments) {
        return new Max(arguments);
    }

    public static NumericAggregateFunction sum(List<Expression<?, ?>> arguments) {
        return new Sum(arguments);
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
        Aggregator<BigDecimal> aggregate = aggregate();
        BigDecimal value = aggregate.zero();
        for (Object argument : arguments) {
            BigDecimal mappedValue = aggregate.map(argument);
            value = aggregate.add(value, mappedValue);
        }
        return value;
    }

    @Override
    public BigDecimal getValue(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        return Expressions.allConstant(arguments);
    }

    public abstract Aggregator<BigDecimal> aggregate();

    @Override
    public String toString() {
        return getClass().getSimpleName().toLowerCase() + "(" + Joiner.on(", ").join(arguments) + ")";
    }

    /*package*/ static class Min extends NumericAggregateFunction {
        public Min(List<Expression<?, ?>> operands) {
            super(operands);
        }

        @Override
        public Aggregator<BigDecimal> aggregate() {
            return ComparableAggregates.MIN;
        }
    }

    /*package*/ static class Max extends NumericAggregateFunction {
        public Max(List<Expression<?, ?>> operands) {
            super(operands);
        }

        @Override
        public Aggregator<BigDecimal> aggregate() {
            return ComparableAggregates.MAX;
        }
    }

    /*package*/ static class Sum extends NumericAggregateFunction {
        public Sum(List<Expression<?, ?>> operands) {
            super(operands);
        }

        @Override
        public Aggregator<BigDecimal> aggregate() {
            return BigDecimalAggregates.SUM;
        }
    }

    private static enum ComparableAggregates implements Aggregator<BigDecimal> {
        MAX() {
            @Override
            public BigDecimal map(Object a) {
                return (BigDecimal)a;
            }

            @Override
            public BigDecimal add(BigDecimal a, BigDecimal b) {
                if (a == null || a.compareTo(b) < 0) {
                    return b;
                } else {
                    return a;
                }
            }

            @Override
            public BigDecimal zero() {
                return null;
            }
        },
        MIN() {
            @Override
            public BigDecimal map(Object a) {
                return (BigDecimal)a;
            }

            @Override
            public BigDecimal add(BigDecimal a, BigDecimal b) {
                if (a == null || a.compareTo(b) > 0) {
                    return b;
                } else {
                    return a;
                }
            }

            @Override
            public BigDecimal zero() {
                return null;
            }
        }
    }

    private static enum BigDecimalAggregates implements Aggregator<BigDecimal> {
        SUM() {
            @Override
            public BigDecimal map(Object a) {
                return (BigDecimal)a;
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
