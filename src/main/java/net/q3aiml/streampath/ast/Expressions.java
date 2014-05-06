package net.q3aiml.streampath.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.q3aiml.streampath.ast.selector.Selector;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Static utility methods for {@link net.q3aiml.streampath.ast.Expression}.
 * @author q3aiml
 */
public final class Expressions {
    /**
     * Returns all {@link net.q3aiml.streampath.ast.selector.Selector} nodes in {@code expression}.
     */
    public static Collection<Selector> selectors(Expression expression) {
        return ImmutableList.copyOf(Iterables.filter(depthFirstIteration(expression), Selector.class));
    }

    /**
     * Returns a collection of all nodes in {@code expression} in depth first order.
     */
    public static Collection<Expression<?>> depthFirstIteration(Expression<?> expression) {
        ImmutableSet.Builder<Expression<?>> nodes = ImmutableSet.builder();
        depthFirstIteration(expression, nodes);
        return nodes.build();
    }

    private static void depthFirstIteration(Expression<?> expression, ImmutableSet.Builder<Expression<?>> nodes) {
        for (StreamPathNode child : expression.children()) {
            depthFirstIteration((Expression)child, nodes);
        }
        nodes.add(expression);
    }

    /**
     * Returns {@code true} if {@link net.q3aiml.streampath.ast.Expression#isConstant()} is true for all nodes
     * in {@code expressions}.
     */
    public static boolean allConstant(Iterable<? extends Expression<?>> expressions) {
        checkArgument(!Iterables.isEmpty(expressions), "expressions must not be empty");

        for (Expression<?> expression : expressions) {
            if (!expression.isConstant()) {
                return false;
            }
        }

        return true;
    }
}
