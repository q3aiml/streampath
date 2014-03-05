package net.q3aiml.streampath.ast.logic;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Keyword;
import net.q3aiml.streampath.ast.Keywords;
import net.q3aiml.streampath.ast.cast.ImplicitCast;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class EqualityExpression extends BooleanExpression<Comparable> {
    private Operation operation;
    private Expression<? extends Comparable> left;
    private Expression<? extends Comparable> right;

    public EqualityExpression(String operation, Expression left, Expression right) {
        this.operation = Keywords.findByRepresentation(operation, Operation.values());
        Expression<? extends Comparable>[] comparables = ImplicitCast.comparable(left, right);
        this.left = comparables[0];
        this.right = comparables[1];
    }

    @Override
    public Expression<Boolean> copy(List<Expression<?>> children) {
        checkArgument(children.size() == 2, "must provide exactly 2 children");
        return new EqualityExpression(operation.representation(), children.get(0), children.get(1));
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(left, right);
    }

    @Override
    public Boolean apply(List<Object> arguments) {
        checkArgument(arguments.size() == 2, "expected two arguments, not %s", arguments.size());
        return operation.apply((Comparable)arguments.get(0), (Comparable)arguments.get(1));
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public String toString() {
        return left + " " + operation + " " + right;
    }

    public enum Operation implements Keyword {
        EQUAL("==") {
            @Override
            public boolean apply(Comparable left, Comparable right) {
                return left != null && left.compareTo(right) == 0;
            }
        },
        NOT_EQUAL("!=") {
            @Override
            public boolean apply(Comparable left, Comparable right) {
                return left != null && left.compareTo(right) != 0;
            }
        },
        ;

        private String representation;

        Operation(String representation) {
            this.representation = representation;
        }

        public abstract boolean apply(Comparable left, Comparable right);

        @Override
        public String representation() {
            return representation;
        }

        public String toString() {
            return representation;
        }
    }
}
