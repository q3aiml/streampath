package net.q3aiml.streampath.ast.selector;

import net.q3aiml.streampath.Document;

import javax.annotation.Nullable;

/**
 * @author q3aiml
 */
public abstract class DocumentSelector implements SelectorBase {
    public abstract boolean accepts(@Nullable Document document);
}
