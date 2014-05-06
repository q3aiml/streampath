package net.q3aiml.streampath.evaluator;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.aggregate.Aggregator;
import net.q3aiml.streampath.ast.aggregate.AggregatorNode;
import net.q3aiml.streampath.ast.selector.DocumentSelector;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.ast.selector.value.ExternalReference;
import net.q3aiml.streampath.ast.selector.value.FrameNavigator;
import net.q3aiml.streampath.ast.selector.value.ValueSelectorNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Aggregates values (mainly from {@link net.q3aiml.streampath.ast.aggregate.AggregatorNode}s), storing their
 * running state.
 * <p/>
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
    private final Evaluator evaluator;

    public Aggregator3000(Iterable<? extends Expression<?>> expressions, Evaluator evaluator) {
        this(findNonConstantAggregatorNodes(expressions, new HashSet<AggregatorNode>()), evaluator);
    }

    public Aggregator3000(Set<AggregatorNode> aggregatorNodes, Evaluator evaluator) {
        ImmutableSet.Builder<SelectorAggregateState> aggregators = ImmutableSet.builder();
        for (AggregatorNode aggregatorNode : aggregatorNodes) {
            aggregators.add(new SelectorAggregateState(aggregatorNode));
        }
        this.aggregators = aggregators.build();
        this.evaluator = evaluator;
    }

    private static Set<AggregatorNode> findNonConstantAggregatorNodes(Iterable<? extends StreamPathNode> nodes,
                                                                      Set<AggregatorNode> aggregatorNodes)
    {
        for (StreamPathNode node : nodes) {
            findNonConstantAggregatorNodes(node, aggregatorNodes, false);
        }
        return aggregatorNodes;
    }

    /**
     * Finds all {@link net.q3aiml.streampath.ast.aggregate.AggregatorNode}s that are not
     * {@link net.q3aiml.streampath.ast.Expression#isConstant()}, creating
     * {@link net.q3aiml.streampath.evaluator.SingleValueConstantAggregatorNode}s for
     * {@link net.q3aiml.streampath.ast.selector.Selector}s that are not aggregated.
     */
    private static Set<AggregatorNode> findNonConstantAggregatorNodes(StreamPathNode node,
                                                                      Set<AggregatorNode> aggregatorNodes,
                                                                      boolean hasAggregateAncestor)
    {
        if (node instanceof AggregatorNode && !((Expression)node).isConstant()) {
            aggregatorNodes.add((AggregatorNode)node);
            hasAggregateAncestor = true;
        }

        if (node instanceof Selector && !hasAggregateAncestor) {
            // if we didn't find any aggregators in our ancestors then we'll assume we are
            // selecting a single value and wrap it in our little dummy single value aggregator
            aggregatorNodes.add(new SingleValueConstantAggregatorNode(ImmutableList.of((Expression)node)));
        }

        if (node instanceof Expression) {
            for (StreamPathNode childExpression : ((Expression<?>)node).children()) {
                findNonConstantAggregatorNodes(childExpression, aggregatorNodes, hasAggregateAncestor);
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

    public SelectorAggregateState getSelectorAggregateState(Selector node) {
        for (SelectorAggregateState aggregatorNodeState : aggregators) {
            if (aggregatorNodeState.selectorWrapper.selector == node) {
                return aggregatorNodeState;
            }
        }
        throw new IllegalArgumentException("value selector not known to aggregator: " + node);
    }

    public ContextValue<Object> getFrameState(Selector node, Frame relativeFrame) {
        MyFrameNavigator startingFrame = new MyFrameNavigator(relativeFrame);
        MyFrameNavigator selectedFrame = (MyFrameNavigator)node.getValueSelector().selector().select(startingFrame);
        return selectedFrame.value();
    }

    public Frame findChild(Frame parentFrame, String match) {
        for (SelectorAggregateState aggregateState : aggregators) {
            for (Frame frame : aggregateState.liveReferencedFramesByParent.values()) {
                if (frame.parent() == parentFrame && Objects.equal(match, frame.name())) {
                    return frame;
                }
            }
        }

        return null;
    }

    private class MyFrameNavigator implements FrameNavigator {
        private Frame frame;
        private QName attributeName;

        private MyFrameNavigator(Frame frame) {
            this(frame, null);
        }

        public MyFrameNavigator(Frame frame, QName attributeName) {
            this.frame = frame;
            this.attributeName = attributeName;
        }

        @Override
        public FrameNavigator attribute(QName name) {
            return new MyFrameNavigator(frame, name);
        }

        @Override
        public FrameNavigator child(String childName) {
            checkState(attributeName == null, "an attribute does not have children (this should have failed in the parser)");
            return new MyFrameNavigator(findChild(frame, childName));
        }

        @Override
        public FrameNavigator parent() {
            if (attributeName == null) {
                return new MyFrameNavigator(frame.parent());
            } else {
                return new MyFrameNavigator(frame);
            }
        }

        public ContextValue<Object> value() {
            if (attributeName == null) {
                if (frame != null) {
                    return ContextValue.<Object>available(frame.contents());
                } else {
                    // don't know any better until we have schema support
                    return ContextValue.willBeAvailable(YesNoMaybe.MAYBE);
                }
            } else {
                Attribute attribute = frame.attributes().attribute(attributeName);
                if (attribute != null) {
                    return ContextValue.<Object>available(attribute.getValue());
                } else {
                    return ContextValue.willBeAvailable(YesNoMaybe.NO);
                }
            }
        }
    }

    /*protected*/ class SelectorAggregateState {
        private AggregatorNode<?> aggregatorNode;
        private Aggregator aggregate;
        private SelectorWrapper selectorWrapper;
        private final Set<ExternalReference> references;

        private Object aggregateValue;

        /// references an element that has not been seen yet (but can still be seen)
        private Set<Frame> delayedEvaluations = new HashSet<Frame>();

        /// frames referenced for later evaluation, by parent frame
        private Multimap<Frame, Frame> liveReferencedFramesByParent = HashMultimap.create();

        private SelectorAggregateState(AggregatorNode aggregatorNode) {
            this.aggregatorNode = aggregatorNode;
            aggregate = aggregatorNode.aggregate();
            aggregateValue = aggregate.zero();
            final SelectorWrapper valueSelector = SelectorWrapper
                    .findValueSelectors(aggregatorNode, new ArrayDeque<Expression<?>>());
            selectorWrapper = checkNotNull(valueSelector,
                    "unable to find value selector under aggregator node " + aggregatorNode);

            references = selectorWrapper.selector.getValueSelector().selector().externalReferences();
        }

        public void frame(Frame frame) {
            final FrameContext frameContext = new FrameContext(evaluator);
            DocumentSelector documentSelector = selectorWrapper.selector.getDocumentSelector();
            if (documentSelector.accepts(frame.document())) {
                ValueSelectorNode valueSelectorNode = selectorWrapper.selector.getValueSelector().selector();
                YesNoMaybe accepts = valueSelectorNode.acceptsRecursive(frame, frameContext);
                if (accepts.isYesOrMaybe()) {
                    if (missingReference(frame)) {
                        delayedEvaluations.add(frame);
                    } else if (accepts.isYes()) {
                        add(frame.contents());
                    }
                }

                if (references(frame)) {
                    liveReferencedFramesByParent.put(frame.parent(), frame);

                    Iterator<Frame> delayedFrames = delayedEvaluations.iterator();
                    while (delayedFrames.hasNext()) {
                        Frame delayedFrame = delayedFrames.next();
                        if (valueSelectorNode.acceptsRecursive(delayedFrame, frameContext).isYes()) {
                            add(delayedFrame.contents());
                            delayedFrames.remove();
                        } else if (!missingReference(delayedFrame)) {
                            delayedFrames.remove();
                        }
                    }
                }
            }

            liveReferencedFramesByParent.removeAll(frame);
        }

        private boolean missingReference(Frame frame) {
            Set<ExternalReference> externalReferences = selectorWrapper.selector.getValueSelector().selector().externalReferences();
            for (ExternalReference externalReference : externalReferences) {
                boolean found = false;
                for (Frame availableFrames : liveReferencedFramesByParent.values()) {
                    if (externalReference.references(availableFrames)) {
                        found = true;
                    }
                }
                if (!found) {
                    return true;
                }
            }
            return false;
        }

        private boolean references(Frame frame) {
            for (ExternalReference reference : references) {
                if (reference.references(frame)) {
                    return true;
                }
            }
            return false;
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

        public Expression aggregatorNode() {
            if (aggregatorNode instanceof SingleValueConstantAggregatorNode) {
                return Iterables.getOnlyElement(aggregatorNode.children());
            }
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
        private Deque<Expression<?>> expressions = new ArrayDeque<Expression<?>>();

        public SelectorWrapper(Selector selector, ArrayDeque<Expression<?>> expressions) {
            this.selector = selector;
            this.expressions = expressions;
        }

        // TODO support more than one value selector
        private static SelectorWrapper findValueSelectors(Expression expression, Deque<Expression<?>> expressionPath) {
            if (expression instanceof Selector) {
                ArrayDeque<Expression<?>> expressions1 = new ArrayDeque<Expression<?>>(expressionPath);
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
