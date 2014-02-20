package net.q3aiml.streampath.ast.selector.value;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.*;
import net.q3aiml.streampath.ast.cast.ImplicitCast;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.evaluator.ContextValue;
import net.q3aiml.streampath.evaluator.Frame;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author q3aiml
 */
public abstract class PredicateFilter extends ValueSelectorNode implements StreamPathNode, Predicate {
    private static final Logger log = LoggerFactory.getLogger(PredicateFilter.class);

    public PredicateFilter(ValueSelectorNode parent) {
        super(parent);
    }

    public static StreamPathNode matching(ValueSelectorNode parent, Expression expression) {
        Expression<Boolean, ?> boolExpression = ImplicitCast.bool(expression);
        return new ExpressionPredicateFilter(parent, boolExpression);
    }

    @Override
    public boolean apply(Object input) {
        return false;
    }

    @Override
    public boolean consumesFrame() {
        return false;
    }

    private static class ExpressionPredicateFilter extends PredicateFilter {
        private final Expression<Boolean, ?> expression;

        public ExpressionPredicateFilter(ValueSelectorNode parent, Expression<Boolean, ?> expression) {
            super(parent);
            this.expression = expression;
        }

        @Override
        protected ValueSelectorNode copy(ValueSelectorNode newParent) {
            return new ExpressionPredicateFilter(newParent, expression);
        }

        @Override
        public YesNoMaybe accepts(Frame frame, Context context) {
            final ContextValue<Boolean> value = context.getValue(frame, expression);
            if (value.isAvailable()) {
                log.trace("{} -> {}", expression, value);
                return YesNoMaybe.of(value.get());
            } else {
                return value.willBeAvailable();
            }
        }

        @Override
        protected Iterable<? extends ExternalReference> immediateExternalReferences() {
            ImmutableSet.Builder<ExternalReference> references = ImmutableSet.builder();

            Collection<Selector> selectors = Expressions.selectors(expression);
            for (Selector selector : selectors) {
                if (!selector.getValueSelector().isExternal()) {
                    continue;
                }

                final ValueSelectorNode resolvedSelector = selector.getValueSelector().selector().resolve(parent);
                log.trace("resolved {} @ {} to {}", selector.getValueSelector().selector(), parent, resolvedSelector);
                references.add(new ExternalReference() {
                    @Override
                    public boolean references(Frame frame) {
                        return resolvedSelector.acceptsRecursive(frame, new FooContext()).isYes();
                    }
                });
            }

            return references.build();
        }

        private static class FooContext implements Context {
            @Override
            public <T> ContextValue<T> getValue(Frame relativeFrame, Expression<T, ?> expression) {
                return ContextValue.willBeAvailable(YesNoMaybe.MAYBE);
            }
        }

        @Override
        public FrameNavigator select(FrameNavigator frame) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return parent + "[" + expression + "]";
        }
    }

}
