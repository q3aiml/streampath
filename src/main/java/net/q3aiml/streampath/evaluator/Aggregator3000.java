package net.q3aiml.streampath.evaluator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.aggregate.Aggregator;
import net.q3aiml.streampath.ast.aggregate.AggregatorNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.ast.selector.ValueSelector;
import net.q3aiml.streampath.ast.selector.value.FrameContext;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *               _  _
 *     _ _      (0)(0)-._  _.-'^^'^^'^^'^^'^^'--.
 *    (.(.)----'`        ^^'                /^   ^^-._
 *    (    `                 \             |    _    ^^-._
 *     VvvvvvvVv~~`__,/.._>  /:/:/:/:/:/:/:/\  (_..,______^^-.
 * jgs  `^^^^^^^^`/  /   /  /`^^^^^^^^^>^^>^`>  >        _`)  )
 *               (((`   (((`          (((`  (((`        `'--'^
 *
 * @author q3aiml (except for the AggreGator)
 */
/*package*/ class Aggregator3000 {
    private static final Logger log = LoggerFactory.getLogger(Aggregator3000.class);

    private final ImmutableSet<ValueSelectorAggregateState> aggregators;

    public Aggregator3000(Iterable<? extends Expression<?, ?>> expressions) {
        this(findNonConstantAggregatorNodes(expressions, new HashSet<AggregatorNode>()));
    }

    public Aggregator3000(Set<AggregatorNode> aggregatorNodes) {
        ImmutableSet.Builder<ValueSelectorAggregateState> aggregators = ImmutableSet.builder();
        for (AggregatorNode aggregatorNode : aggregatorNodes) {
            aggregators.add(new ValueSelectorAggregateState(aggregatorNode));
        }
        this.aggregators = aggregators.build();
    }

    private static Set<AggregatorNode> findNonConstantAggregatorNodes(Iterable<? extends StreamPathNode> nodes, Set<AggregatorNode> aggregatorNodes) {
        for (StreamPathNode node : nodes) {
            findNonConstantAggregatorNodes(node, aggregatorNodes);
        }
        return aggregatorNodes;
    }

    private static Set<AggregatorNode> findNonConstantAggregatorNodes(StreamPathNode node, Set<AggregatorNode> aggregatorNodes) {
        if (node instanceof AggregatorNode && !((Expression)node).isConstant()) {
            aggregatorNodes.add((AggregatorNode)node);
        }

        if (node instanceof Expression) {
            for (StreamPathNode childExpression : ((Expression<?, ?>)node).children()) {
                findNonConstantAggregatorNodes(childExpression, aggregatorNodes);
            }
        }
        return aggregatorNodes;
    }

    public void frame(Frame frame) {
        for (ValueSelectorAggregateState aggregator : aggregators) {
            aggregator.frame(frame);
        }
    }

    @Override
    public String toString() {
        return "Aggregator3000{" +
                "aggregators=" + aggregators +
                '}';
    }

    public ValueSelectorAggregateState getValueSelectorNodeState(ValueSelector node) {
        for (ValueSelectorAggregateState aggregatorNodeState : aggregators) {
            if (aggregatorNodeState.valueSelectors.valueSelector == node) {
                return aggregatorNodeState;
            }
        }
        throw new IllegalArgumentException("value selector not known to aggregator: " + node);
    }

    /*protected*/ static class ValueSelectorAggregateState {
        private AggregatorNode aggregatorNode;
        private Aggregator aggregate;
        private ValueSelectors valueSelectors;

        private Object aggregateValue;

        private ValueSelectorAggregateState(AggregatorNode aggregatorNode) {
            this.aggregatorNode = aggregatorNode;
            aggregate = aggregatorNode.aggregate();
            aggregateValue = aggregate.zero();
            final ValueSelectors valueSelectors = ValueSelectors
                    .findValueSelectors(aggregatorNode, new ArrayDeque<Expression<?, ?>>());
            this.valueSelectors = checkNotNull(valueSelectors,
                    "unable to find value selector under aggregator node " + aggregatorNode);
        }

        public void frame(Frame frame) {
            final FrameContext frameContext = new FrameContext(frame);
            if (valueSelectors.valueSelector.selector().acceptsRecursive(frame, frameContext)) {
                add(frame.contents());
            }
        }

        private void add(String value) {
            checkNotNull(value);

            Object transformedValue = valueSelectors.applyTransforms(value);
            Object mappedValue = aggregate.map(transformedValue);
            aggregateValue = aggregate.add(aggregateValue, mappedValue);
        }

        public Object get() {
            return aggregateValue;
        }

        public AggregatorNode aggregatorNode() {
            return aggregatorNode;
        }

        @Override
        public String toString() {
            return "ValueSelectorAggregateState{" +
                    "aggregatorNode=" + aggregatorNode +
                    ", \n\taggregate=" + aggregate +
                    ", \n\tvalueSelectors=" + valueSelectors +
                    ", \n\taggregateValue=" + aggregateValue +
                    '}';
        }
    }

    private static class ValueSelectors {
        private ValueSelector valueSelector;
        private Deque<Expression<?, ?>> expressions = new ArrayDeque<Expression<?, ?>>();

        public ValueSelectors(ValueSelector valueSelector, ArrayDeque<Expression<?, ?>> expressions) {
            this.valueSelector = valueSelector;
            this.expressions = expressions;
        }

        // TODO support more than one value selector
        private static ValueSelectors findValueSelectors(Expression expression, Deque<Expression<?, ?>> expressionPath) {
            if (expression instanceof Selector) {
                ArrayDeque<Expression<?, ?>> expressions1 = new ArrayDeque<Expression<?, ?>>(expressionPath);
                expressions1.removeFirst();
                return new ValueSelectors(((Selector)expression).getValueSelector(), expressions1);
            }

            ValueSelectors valueSelectors = null;

            for (Object child : expression.children()) {
                expressionPath.addFirst((Expression)child);
                ValueSelectors childSelectors = findValueSelectors((Expression) child, expressionPath);
                expressionPath.removeFirst();

                if (childSelectors != null) {
                    if (valueSelectors != null) {
                        throw new UnsupportedOperationException("TODO");
                    }

                    valueSelectors = childSelectors;
                }
            }

            return valueSelectors;
        }

        private Object applyTransforms(Object value) {
            for (Expression expression : expressions) {
                if (expression.children().size() > 1) {
                    Expression biggestHackYet = (Expression)expression.children().get(1);
                    value = expression.apply(ImmutableList.of(value, biggestHackYet.apply(ImmutableList.of())));
                } else {
                    value = expression.apply(value != null ? ImmutableList.of(value) : ImmutableList.of());
                }
            }
            return value;
        }
    }

}
