package net.q3aiml.streampath.ast.selector;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.literal.Literal;

import java.util.List;

/**
 * @author q3aiml
 */
public class DocumentSelector implements SelectorBase {
    private final String selector;
    private final List<Literal> arguments;

    public DocumentSelector(String selector, List<Literal> arguments) {
        this.selector = selector;
        this.arguments = arguments;
    }

    public DocumentSelector() {
        this.selector = null;
        this.arguments = ImmutableList.of();
    }

    public String selector() {
        return selector;
    }

    @Override
    public String toString() {
        return selector + "(" + Joiner.on(", ").join(arguments) + ")";
    }
}
