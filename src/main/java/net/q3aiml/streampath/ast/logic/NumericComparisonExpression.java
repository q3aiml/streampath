package net.q3aiml.streampath.ast.logic;

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
public class NumericComparisonExpression extends BooleanExpression<BigDecimal> {
    private Operation operation;
    private Expression<BigDecimal> left;
    private Expression<BigDecimal> right;

    public NumericComparisonExpression(String operation, Expression left, Expression right) {
        this.operation = Keywords.findByRepresentation(operation, Operation.values());
        this.left = ImplicitCast.numeric(left);
        this.right = ImplicitCast.numeric(right);
    }

    @Override
    public Expression<Boolean> copy(List<Expression<?>> children) {
        checkArgument(children.size() == 2, "must provide exactly 2 children");
        return new NumericComparisonExpression(operation.representation(), children.get(0), children.get(1));
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(left, right);
    }

    @Override
    public Boolean apply(List<Object> arguments) {
        checkArgument(arguments.size() == 2, "expected two arguments, not %s", arguments.size());
        return operation.apply((BigDecimal)arguments.get(0), (BigDecimal)arguments.get(1));
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
        GREATER_THAN(">") {
            @Override
            public boolean apply(BigDecimal left, BigDecimal right) {
                return left.compareTo(right) > 0;
            }
        },
        LESS_THAN("<") {
            @Override
            public boolean apply(BigDecimal left, BigDecimal right) {
                return left.compareTo(right) < 0;
            }
        },
        GREATER_THAN_EQUAL_TO(">=") {
            @Override
            public boolean apply(BigDecimal left, BigDecimal right) {
                return left.compareTo(right) >= 0;
            }
        },
        LESS_THAN_EQUAL_TO("<=") {
            @Override
            public boolean apply(BigDecimal left, BigDecimal right) {
                return left.compareTo(right) <= 0;
            }
        },
        ;

        private String representation;

        Operation(String representation) {
            this.representation = representation;
        }

        public abstract boolean apply(BigDecimal left, BigDecimal right);

        @Override
        public String representation() {
            return representation;
        }

        public String toString() {
            return representation;
        }
    }
}
