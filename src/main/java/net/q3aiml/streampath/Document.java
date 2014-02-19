package net.q3aiml.streampath;

import javax.annotation.Nullable;
import javax.xml.stream.XMLEventReader;
import java.io.Closeable;
import java.io.IOException;

/**
 * @author q3aiml
 */
public interface Document extends Closeable {
    @Nullable
    public String identifier();

    /**
     * Contents of the document as a XMLEvent stream.
     */
    public XMLEventReader stream() throws IOException;
}
