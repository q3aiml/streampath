package net.q3aiml.streampath;

import javax.xml.stream.XMLEventReader;
import java.io.Closeable;
import java.io.IOException;

/**
 * @author q3aiml
 */
public interface Document extends Closeable {
    public XMLEventReader stream() throws IOException;
}
