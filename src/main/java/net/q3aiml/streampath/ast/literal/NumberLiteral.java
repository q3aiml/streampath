package net.q3aiml.streampath.ast.literal;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class NumberLiteral extends Literal<BigDecimal> {
    private final BigDecimal value;

    public NumberLiteral(BigDecimal value) {
        this.value = value;
    }

    public NumberLiteral(String value) {
        this(new BigDecimal(value));
    }

    @Override
    public Class<BigDecimal> getValueType() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal apply(List<Object> arguments) {
        checkArgument(arguments.size() == 0, "expected no arguments, not %s", arguments.size());
        return value;
    }

    @Override
    public String asString() {
        return value.toPlainString();
    }

    @Override
    public String toString() {
        return "Literal{" + value + "}";
    }
}
