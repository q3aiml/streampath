package net.q3aiml.streampath;

/**
 * Thrown when there is a general streampath error. May be subclasses for more specific issues.
 * @author q3aiml
 */
public class StreamPathException extends Exception {
    public StreamPathException(String message) {
        super(message);
    }

    public StreamPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamPathException(Throwable cause) {
        super(cause);
    }
}
