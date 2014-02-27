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
public class BinaryBooleanExpression extends BooleanExpression<Boolean> {
    private Operation operation;
    private Expression<Boolean> left;
    private Expression<Boolean> right;

    public BinaryBooleanExpression(String operation, Expression left, Expression right) {
        this.operation = Keywords.findByRepresentation(operation, Operation.values());
        this.left = ImplicitCast.bool(left);
        this.right = ImplicitCast.bool(right);
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(left, right);
    }

    @Override
    public Boolean apply(List<Object> arguments) {
        checkArgument(arguments.size() == 2, "expected two arguments, not %s", arguments.size());
        return operation.apply((Boolean)arguments.get(0), (Boolean)arguments.get(1));
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
        AND("and") {
            @Override
            public boolean apply(boolean left, boolean right) {
                return left && right;
            }
        },
        OR("or") {
            @Override
            public boolean apply(boolean left, boolean right) {
                return left || right;
            }
        },
        ;

        private String representation;

        Operation(String representation) {
            this.representation = representation;
        }

        public abstract boolean apply(boolean left, boolean right);

        @Override
        public String representation() {
            return representation;
        }

        public String toString() {
            return representation;
        }
    }
}
