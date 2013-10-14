package net.q3aiml.streampath.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.q3aiml.streampath.ast.selector.Selector;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public final class Expressions {
    public static Collection<Selector> selectors(Expression expression) {
        return ImmutableList.copyOf(Iterables.filter(depthFirstIteration(expression), Selector.class));
    }

    public static Collection<Expression<?, ?>> depthFirstIteration(Expression<?, ?> expression) {
        ImmutableSet.Builder<Expression<?, ?>> nodes = ImmutableSet.builder();
        depthFirstIteration(expression, nodes);
        return nodes.build();
    }

    private static void depthFirstIteration(Expression<?, ?> expression, ImmutableSet.Builder<Expression<?, ?>> nodes) {
        for (StreamPathNode child : expression.children()) {
            depthFirstIteration((Expression)child, nodes);
        }
        nodes.add(expression);
    }

    public static boolean allConstant(Iterable<? extends Expression<?, ?>> expressions) {
        checkArgument(!Iterables.isEmpty(expressions), "expressions must not be empty");

        for (Expression<?, ?> expression : expressions) {
            if (!expression.isConstant()) {
                return false;
            }
        }

        return true;
    }
}
