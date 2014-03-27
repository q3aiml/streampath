package net.q3aiml.streampath.ast.selector;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.streampath.ast.DocumentSelectorFactory;
import net.q3aiml.streampath.ast.literal.Literal;

import java.util.List;
import java.util.Map;

/**
 * @author ajclayton
 */
public class SelectorsModule {
    private static final Map<? extends String, ? extends DocumentSelectorFactory> FACTORIES = ImmutableMap.of(
            "document", new DocumentSelectorFactory() {
                @Override
                public DocumentSelector create(List<Literal> arguments) {
                    return new IdentifierDocumentSelector(arguments);
                }
            }
    );

    public static Map<? extends String, ? extends DocumentSelectorFactory> defaultDocumentSelectorFactories() {
        return FACTORIES;
    }
}
