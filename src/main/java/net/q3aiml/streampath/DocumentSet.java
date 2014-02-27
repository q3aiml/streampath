package net.q3aiml.streampath;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

/**
 * @author q3aiml
 */
public interface DocumentSet extends Closeable {
    /**
     * The default document that expressions refer to if they do not explicitly refer to a document.
     */
    public Document currentDocument();

    /**
     * All documents including {@link #currentDocument()}.
     */
    public Set<Document> documents();

    /**
     * Closes all documents in this set as well as the set itself.
     * <p/>
     * The document set should not be used after being closed.
     */
    @Override
    void close() throws IOException;
}
