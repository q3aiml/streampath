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
public class IdentifierDocumentSelector extends DocumentSelector {
    private final List<Literal> arguments;

    public IdentifierDocumentSelector(List<Literal> arguments) {
        this.arguments = arguments;
    }

    /**
     * Selects the default/current document.
     */
    public IdentifierDocumentSelector() {
        this.arguments = ImmutableList.of();
    }

    private boolean acceptsDocumentIdentifier(String identifier) {
        if (arguments.size() == 1) {
            String literalValue = arguments.get(0).asString();
            return equal(literalValue, identifier);
        }
        return false;
    }

    public boolean accepts(@Nullable Document document) {
        return (arguments.isEmpty() && (document == null || isNullOrEmpty(document.identifier())))
                || (document != null && acceptsDocumentIdentifier(document.identifier()));
    }

    @Override
    public String toString() {
        return "document(" + Joiner.on(", ").join(arguments) + ")";
    }
}

