package net.q3aiml.streampath.ast.selector;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.Document;
import net.q3aiml.streampath.ast.literal.Literal;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.isNullOrEmpty;

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

    private boolean acceptsDocumentIdentifier(String identifier) {
        if ("document".equals(selector) && arguments.size() == 1) {
            Object literalValue = arguments.get(0).getValue(null);
            if (literalValue != null) {
                return equal(literalValue.toString(), identifier);
            }
        }
        return false;
    }

    public boolean accepts(@Nullable Document document) {
        return (isNullOrEmpty(selector) && (document == null || isNullOrEmpty(document.identifier())))
                || (document != null && acceptsDocumentIdentifier(document.identifier()));
    }

    @Override
    public String toString() {
        return selector + "(" + Joiner.on(", ").join(arguments) + ")";
    }
}
