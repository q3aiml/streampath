package net.q3aiml.streampath.evaluator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.aggregate.Aggregator;
import net.q3aiml.streampath.ast.aggregate.AggregatorNode;
import net.q3aiml.streampath.ast.selector.DocumentSelector;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.ast.selector.ValueSelector;
import net.q3aiml.streampath.ast.selector.value.FrameContext;
import net.q3aiml.streampath.ast.selector.value.ValueSelectorNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final ImmutableSet<SelectorAggregateState> aggregators;

    public Aggregator3000(Iterable<? extends Expression<?, ?>> expressions) {
        this(findNonConstantAggregatorNodes(expressions, new HashSet<AggregatorNode>()));
    }

    public Aggregator3000(Set<AggregatorNode> aggregatorNodes) {
        ImmutableSet.Builder<SelectorAggregateState> aggregators = ImmutableSet.builder();
        for (AggregatorNode aggregatorNode : aggregatorNodes) {
            aggregators.add(new SelectorAggregateState(aggregatorNode));
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
        for (SelectorAggregateState aggregator : aggregators) {
            aggregator.frame(frame);
        }
    }

    @Override
    public String toString() {
        return "Aggregator3000{" +
                "aggregators=" + aggregators +
                '}';
    }

    public SelectorAggregateState getValueSelectorNodeState(Selector node) {
        for (SelectorAggregateState aggregatorNodeState : aggregators) {
            if (aggregatorNodeState.selectorWrapper.selector == node) {
                return aggregatorNodeState;
            }
        }
        throw new IllegalArgumentException("value selector not known to aggregator: " + node);
    }

    /*protected*/ static class SelectorAggregateState {
        private AggregatorNode aggregatorNode;
        private Aggregator aggregate;
        private SelectorWrapper selectorWrapper;

        private Object aggregateValue;

        private SelectorAggregateState(AggregatorNode aggregatorNode) {
            this.aggregatorNode = aggregatorNode;
            aggregate = aggregatorNode.aggregate();
            aggregateValue = aggregate.zero();
            final SelectorWrapper valueSelector = SelectorWrapper
                    .findValueSelectors(aggregatorNode, new ArrayDeque<Expression<?, ?>>());
            selectorWrapper = checkNotNull(valueSelector,
                    "unable to find value selector under aggregator node " + aggregatorNode);
        }

        public void frame(Frame frame) {
            final FrameContext frameContext = new FrameContext(frame);
            DocumentSelector documentSelector = selectorWrapper.selector.getDocumentSelector();
            if (documentSelector.accepts(frame.document())) {
                ValueSelectorNode valueSelectorNode = selectorWrapper.selector.getValueSelector().selector();
                if (valueSelectorNode.acceptsRecursive(frame, frameContext)) {
                    add(frame.contents());
                }
            }
        }

        private void add(String value) {
            checkNotNull(value);

            Object transformedValue = selectorWrapper.applyTransforms(value);
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
            return "SelectorAggregateState{" +
                    "aggregatorNode=" + aggregatorNode +
                    ", \n\taggregate=" + aggregate +
                    ", \n\tselectorWrapper=" + selectorWrapper +
                    ", \n\taggregateValue=" + aggregateValue +
                    '}';
        }
    }

    private static class SelectorWrapper {
        private Selector selector;
        private Deque<Expression<?, ?>> expressions = new ArrayDeque<Expression<?, ?>>();

        public SelectorWrapper(Selector selector, ArrayDeque<Expression<?, ?>> expressions) {
            this.selector = selector;
            this.expressions = expressions;
        }

        // TODO support more than one value selector
        private static SelectorWrapper findValueSelectors(Expression expression, Deque<Expression<?, ?>> expressionPath) {
            if (expression instanceof Selector) {
                ArrayDeque<Expression<?, ?>> expressions1 = new ArrayDeque<Expression<?, ?>>(expressionPath);
                expressions1.removeFirst();
                return new SelectorWrapper((Selector)expression, expressions1);
            }

            SelectorWrapper selectorWrapper = null;

            for (Object child : expression.children()) {
                expressionPath.addFirst((Expression)child);
                SelectorWrapper childSelectorWrapper = findValueSelectors((Expression)child, expressionPath);
                expressionPath.removeFirst();

                if (childSelectorWrapper != null) {
                    if (selectorWrapper != null) {
                        throw new UnsupportedOperationException("TODO");
                    }

                    selectorWrapper = childSelectorWrapper;
                }
            }

            return selectorWrapper;
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
