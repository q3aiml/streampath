package net.q3aiml.streampath.ast.literal;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;

import java.util.List;

/**
 * @author q3aiml
 */
public abstract class Literal<T> implements Expression<T, Void> {
    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of();
    }
}
