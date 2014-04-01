package net.q3aiml.streampath.ast.arithmetic;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Keyword;
import net.q3aiml.streampath.ast.Keywords;
import net.q3aiml.streampath.ast.cast.ImplicitCast;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class ArithmeticExpression implements Expression<BigDecimal> {
    private Operation operation;
    private Expression<BigDecimal> left;
    private Expression<BigDecimal> right;

    public ArithmeticExpression(String operation, Expression<?> left, Expression<?> right) {
        this.operation = Keywords.findByRepresentation(operation, Operation.values());
        this.left = ImplicitCast.numeric(left);
        this.right = ImplicitCast.numeric(right);
    }

    @Override
    public Expression<BigDecimal> copy(List<Expression<?>> children) {
        checkArgument(children.size() == 2, "must provide exactly 2 children");
        return new ArithmeticExpression(operation.representation(), children.get(0), children.get(1));
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(left, right);
    }

    @Override
    public Class<BigDecimal> getValueType() {
        return BigDecimal.class;
    }

    public BigDecimal apply(List<Object> arguments) {
        checkArgument(arguments.size() == 2, "expected two arguments, not %s", arguments.size());
        return operation.apply((BigDecimal)arguments.get(0), (BigDecimal)arguments.get(1));
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toVerboseString() {
        return toString();
    }

    @Override
    public String toString() {
        return left + " " + operation + " " + right;
    }

    public enum Operation implements Keyword {
        MULTIPLY("*") {
            @Override
            public BigDecimal apply(BigDecimal left, BigDecimal right) {
                return left.multiply(right);
            }
        },
        DIVIDE("/") {
            @Override
            public BigDecimal apply(BigDecimal left, BigDecimal right) {
                return left.divide(right);
            }
        },

        ADD("+") {
            @Override
            public BigDecimal apply(BigDecimal left, BigDecimal right) {
                return left.add(right);
            }
        },
        SUBTRACT("-") {
            @Override
            public BigDecimal apply(BigDecimal left, BigDecimal right) {
                return left.subtract(right);
            }
        },
        ;

        private String representation;

        Operation(String representation) {
            this.representation = representation;
        }

        public abstract BigDecimal apply(BigDecimal left, BigDecimal right);

        @Override
        public String representation() {
            return representation;
        }

        public String toString() {
            return representation;
        }
    }
}
