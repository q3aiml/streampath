package net.q3aiml.streampath;

/**
 * Thrown when a document provided to streampath is invalid.
 * @author ajclayton
 */
public class InvalidDocumentException extends StreamPathException {
    public InvalidDocumentException(String message) {
        super(message);
    }

    public InvalidDocumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDocumentException(Throwable cause) {
        super(cause);
    }
}
