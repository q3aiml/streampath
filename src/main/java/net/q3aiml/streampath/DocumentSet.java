package net.q3aiml.streampath;

import java.util.Set;

/**
 * @author q3aiml
 */
public interface DocumentSet {
    public Document currentDocument();

    public Set<Document> documents();
}
