package net.q3aiml.streampath;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

/**
 * @author q3aiml
 */
public interface DocumentSet extends Closeable {
    /**
     * All documents in the set.
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
