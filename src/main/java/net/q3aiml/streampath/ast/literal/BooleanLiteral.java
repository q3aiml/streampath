package net.q3aiml.streampath.ast.literal;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class BooleanLiteral extends Literal<Boolean> {
    private final boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Override
    public Boolean apply(List<Object> arguments) {
        checkArgument(arguments.size() == 0, "expected no arguments, not %s", arguments.size());
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
