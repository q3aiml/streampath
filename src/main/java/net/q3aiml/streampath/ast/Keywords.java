package net.q3aiml.streampath.ast;

/**
 * Maps between a string representation and an object
 * @author q3aiml
 */
public final class Keywords {
    /**
     * Returns the keyword with the given string representation.
     * @throws java.lang.IllegalArgumentException if none of the {@code keywords} match
     */
    public static <T extends Keyword> T findByRepresentation(String representation, T... keywords) {
        for (T keyword : keywords) {
            if (keyword.representation().equals(representation)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("unknown keyword " + representation);
    }
}
