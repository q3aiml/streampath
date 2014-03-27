package net.q3aiml.streampath.ast;

import net.q3aiml.streampath.ast.literal.Literal;
import net.q3aiml.streampath.ast.selector.DocumentSelector;

import java.util.List;

/**
 * Creates {@link DocumentSelector}s from arguments.
 * @author ajclayton
 */
public interface DocumentSelectorFactory {
    public DocumentSelector create(List<Literal> arguments);
}
