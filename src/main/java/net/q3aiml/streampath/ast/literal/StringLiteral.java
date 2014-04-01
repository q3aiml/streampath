package net.q3aiml.streampath.ast.literal;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class StringLiteral extends Literal<String> {
    public String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Class<String> getValueType() {
        return String.class;
    }

    @Override
    public String apply(List<Object> arguments) {
        checkArgument(arguments.size() == 0, "expected no arguments, not %s", arguments.size());
        return value;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
