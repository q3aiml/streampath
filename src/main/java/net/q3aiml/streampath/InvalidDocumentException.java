package net.q3aiml.streampath;

/**
 * Thrown when a document provided to streampath is invalid.
 * @author ajclayton
 */
public class InvalidDocumentException extends StreamPathException {
    private final Document document;

    public InvalidDocumentException(Document document, Throwable cause) {
        super("document " + document + " was not in the expected format", cause);
        this.document = document;
    }

    public InvalidDocumentException(String message, Document document, Throwable cause) {
        super(message, cause);
        this.document = document;
    }

    public Document document() {
        return document;
    }
}
