package net.q3aiml.streampath.ast.literal;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;

import java.util.List;

/**
 * @author q3aiml
 */
public abstract class Literal<T> implements Expression<T> {
    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    /**
     * The value of this literal as a string without any quoting.
     */
    public abstract String asString();
}
