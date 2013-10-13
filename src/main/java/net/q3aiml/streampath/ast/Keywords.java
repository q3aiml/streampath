package net.q3aiml.streampath.ast;

/**
 * @author q3aiml
 */
public final class Keywords {
    public static <T extends Keyword> T findByRepresentation(String representation, T... keywords) {
        for (T keyword : keywords) {
            if (keyword.representation().equals(representation)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("unknown keyword " + representation);
    }
}
