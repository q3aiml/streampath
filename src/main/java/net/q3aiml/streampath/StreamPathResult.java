package net.q3aiml.streampath;

import java.util.Set;

/**
 * @author ajclayton
 */
public interface StreamPathResult {
    /**
     * @throws IllegalArgumentException if {@code expression} is not part of the results.
     */
    public Object result(String expression);

    public Set<String> expressions();
}
