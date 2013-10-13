package net.q3aiml.streampath.ast.logic;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Keyword;
import net.q3aiml.streampath.ast.Keywords;
import net.q3aiml.streampath.ast.cast.ImplicitCast;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class UnaryBooleanExpression extends BooleanExpression<Boolean> {
    private Operation operation;
    private Expression<Boolean, ?> operand;

    public UnaryBooleanExpression(String operation, Expression operand) {
        this.operation = Keywords.findByRepresentation(operation, Operation.values());
        this.operand = ImplicitCast.bool(operand);
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(operand);
    }

    @Override
    public Boolean apply(List<Object> arguments) {
        checkArgument(arguments.size() == 1, "expected one argument, not %s", arguments.size());
        return operation.apply((Boolean)arguments.get(0));
    }

    @Override
    public Boolean getValue(Context context) {
        return operation.apply(operand.getValue(context));
    }

    @Override
    public String toString() {
        return operation + " " + operand;
    }

    public enum Operation implements Keyword {
        NOT("not") {
            @Override
            public boolean apply(boolean operand) {
                return !operand;
            }
        },
        ;

        private String representation;

        Operation(String representation) {
            this.representation = representation;
        }

        public abstract boolean apply(boolean operand);

        @Override
        public String representation() {
            return representation;
        }

        public String toString() {
            return representation;
        }
    }
}
