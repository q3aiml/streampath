package net.q3aiml.streampath.ast;

import java.util.List;

/**
 * @author q3aiml
 */
public interface Expression<T, I> extends StreamPathNode {
    public List<? extends Expression> children();

    public Class<T> getValueType();

    public T apply(List<Object> arguments);

    public T getValue(Context context);
}
