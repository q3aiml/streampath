package net.q3aiml.streampath.evaluator;

import net.q3aiml.streampath.Document;

import javax.annotation.Nullable;

/**
 * @author q3aiml
 */
public interface Frame {
    @Nullable
    public Document document();

    /**
     * @see #isRoot()
     */
    public Frame parent();

    /**
     * In the case of XML, the tag name.
     * @throws IllegalStateException if {@link #isRoot()} is {@code true}
     */
    public String name();

    /**
     * Returns {@code true} if this frame has no parent.
     * @see #parent()
     */
    public boolean isRoot();

    public String contents();

    public AttributeCollection attributes();
}
