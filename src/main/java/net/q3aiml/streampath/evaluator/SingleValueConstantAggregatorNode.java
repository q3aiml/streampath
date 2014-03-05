package net.q3aiml.streampath.evaluator;

import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.aggregate.Aggregator;
import net.q3aiml.streampath.ast.aggregate.AggregatorNode;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/*protected*/ class SingleValueConstantAggregatorNode extends AggregatorNode<Object> {
    private final List<? extends Expression> children;

    public SingleValueConstantAggregatorNode(List<? extends Expression> children) {
        this.children = children;
    }

    @Override
    public Aggregator<Object> aggregate() {
        return new SingleValueConstantAggregator();
    }

    @Override
    public Expression<Object> copy(List<Expression<?>> children) {
        return new SingleValueConstantAggregatorNode(children);
    }

    @Override
    public List<? extends Expression> children() {
        return children;
    }

    @Override
    public Class<Object> getValueType() {
        return Object.class;
    }

    @Override
    public Object apply(List<Object> arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    private static class SingleValueConstantAggregator implements Aggregator<Object> {
        @Override
        public Object map(Object a) {
            return checkNotNull(a, "value cannot be null");
        }

        @Override
        public Object add(Object a, Object b) {
            if (a == null) {
                return b;
            } else if (b == null) {
                return a;
            } else {
                throw new UnsupportedOperationException("expected only a single value");
            }
        }

        @Override
        public Object zero() {
            return null;
        }
    }
}
