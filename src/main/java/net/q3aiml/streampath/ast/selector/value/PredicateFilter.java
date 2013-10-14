package net.q3aiml.streampath.ast.selector.value;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Expressions;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.evaluator.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.cast.ImplicitCast;

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
    public Object selectSingle(Context context) {
        throw new UnsupportedOperationException();
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
        public boolean accepts(Frame frame, FrameContext frameContext) {
            final Boolean value = expression.getValue(frameContext);
            log.trace("{} -> {}", expression, value);
            return value;
        }

        public String toString() {
            return parent + "[" + expression + "]";
        }
    }

}
