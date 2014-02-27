package net.q3aiml.streampath.ast;

import java.util.List;

/**
 * @author q3aiml
 */
public interface Expression<T> extends StreamPathNode {
    public List<? extends Expression> children();

    public Class<T> getValueType();

    public T apply(List<Object> arguments);

    public boolean isConstant();
}
