package net.q3aiml.streampath;

import java.util.Set;

/**
 * @author q3aiml
 */
public interface DocumentSet {
    /**
     * The default document that expressions refer to if they do not explicitly refer to a document.
     */
    public Document currentDocument();

    /**
     * All documents including {@link #currentDocument()}.
     */
    public Set<Document> documents();
}
