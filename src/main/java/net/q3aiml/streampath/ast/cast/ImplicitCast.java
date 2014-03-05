package net.q3aiml.streampath.ast.cast;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Type;
import net.q3aiml.streampath.ast.literal.Symbol;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public abstract class ImplicitCast<T> implements Expression<T> {
    protected final Type type;
    protected final Expression operand;

    private ImplicitCast(Type type, Expression operand) {
        this.type = type;
        this.operand = operand;
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of(operand);
    }

    @Override
    public Class<T> getValueType() {
        return type.getTypeClass();
    }

    @Override
    public boolean isConstant() {
        return operand.isConstant();
    }

    public static Expression<BigDecimal> numeric(Expression operand) {
        if (operand.getValueType() == BigDecimal.class) {
            //noinspection unchecked
            return operand;
        } else {
            return new ImplicitNumericCast(operand);
        }
    }

    public static Expression<String> string(Expression operand) {
        if (operand.getValueType() == String.class) {
            //noinspection unchecked
            return operand;
        } else {
            return new ImplicitStringCast(operand);
        }
    }

    public static Expression<Boolean> bool(Expression operand) {
        if (operand.getValueType() == Boolean.class) {
            //noinspection unchecked
            return operand;
        } else {
            return new ImplicitBooleanCast(operand);
        }
    }

    public static Expression<? extends Comparable>[] comparable(Expression... operands) {
        boolean compatibleTypes = true;
        Class type = null;

        for (int i = 0; i < operands.length; i++) {
            if (operands[i] == null) {
                throw new NullPointerException("operand " + i + " is null");
            }

            Class valueType = operands[i].getValueType();
            if (type != null && type != valueType) {
                compatibleTypes = false;
            }

            if (type == null) {
                type = valueType;
            } else if (valueType == BigDecimal.class) {
                if (type == String.class) {
                    // try to compare numbers as numbers
                    type = BigDecimal.class;
                }
            } else if (type != valueType) {
                // otherwise fall back to string if they don't match
                type = String.class;
            }
        }

        if (compatibleTypes) {
            //noinspection unchecked
            return (Expression<Comparable>[])operands;
        }


        Expression<? extends Comparable>[] comparables = (Expression<? extends Comparable>[])new Expression[operands.length];
        for (int i = 0; i < operands.length; i++) {
            if (type == BigDecimal.class) {
                comparables[i] = numeric(operands[i]);
            } else if (type == String.class) {
                comparables[i] = string(operands[i]);
            } else {
                throw new UnsupportedOperationException("cannot convert to " + type);
            }
        }
        return comparables;
    }

    @Override
    public String toString() {
        return "ImplicitCast<" + type + ">{ " + operand + " }";
    }

    public static class ImplicitNumericCast extends ImplicitCast<BigDecimal> {
        public ImplicitNumericCast(Expression operand) {
            super(Type.NUMERIC, operand);
        }

        @Override
        public Expression<BigDecimal> copy(List<Expression<?>> children) {
            checkArgument(children.size() == 2, "must provide exactly one child");
            return new ImplicitNumericCast(children.get(0));
        }

        @Override
        public BigDecimal apply(List<Object> arguments) {
            checkArgument(arguments.size() == 1, "expected one argument, not %s", arguments.size());
            Object value = arguments.get(0);

            if (value instanceof BigDecimal) {
                return (BigDecimal)value;
            } else if (value instanceof String) {
                return new BigDecimal((String)value);
            } else {
                throw new UnsupportedOperationException("unable to convert " + value + " (" + value.getClass() + ") to numeric");
            }
        }

    }

    public static class ImplicitStringCast extends ImplicitCast<String> {
        public ImplicitStringCast(Expression operand) {
            super(Type.STRING, operand);
        }

        @Override
        public Expression<String> copy(List<Expression<?>> children) {
            checkArgument(children.size() == 2, "must provide exactly one child");
            return new ImplicitStringCast(children.get(0));
        }

        @Override
        public String apply(List<Object> arguments) {
            checkArgument(arguments.size() == 1, "expected one argument, not %s", arguments.size());
            Object value = arguments.get(0);

            if (value instanceof String) {
                return (String)value;
            } else if (value instanceof BigDecimal) {
                return ((BigDecimal)value).toPlainString();
            } else if (value == Symbol.NULL) {
                return null;
            } else {
                throw new UnsupportedOperationException("unable to convert " + value + " (" + value.getClass() + ") "
                        + "to string"
                        + "\n\toperand: " + operand);
            }
        }

    }

    public static class ImplicitBooleanCast extends ImplicitCast<Boolean> {
        public ImplicitBooleanCast(Expression operand) {
            super(Type.BOOLEAN, operand);
        }

        @Override
        public Expression<Boolean> copy(List<Expression<?>> children) {
            checkArgument(children.size() == 1, "must provide exactly one child");
            return new ImplicitBooleanCast(children.get(0));
        }

        @Override
        public Boolean apply(List<Object> arguments) {
            checkArgument(arguments.size() == 1, "expected one argument, not %s", arguments.size());
            Object value = arguments.get(0);

            if (value instanceof Boolean) {
                return (Boolean)value;
            } else if (value == Symbol.NULL) {
                return false;
            } else {
                throw new UnsupportedOperationException("unable to convert " + value + " (" + value.getClass() + ") "
                        + "to boolean"
                        + "\n\toperand: " + operand);
            }
        }

    }
}
