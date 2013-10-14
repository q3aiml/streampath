package net.q3aiml.streampath.ast.aggregate;

/**
 * A strategy for calculating an aggregate measure, such as min or max, over zero or more values.
 * @author q3aiml
 */
public interface Aggregator<T> {
    public T map(Object a);

    /**
     * Adds together two values.
     */
    public T add(T a, T b);

    /**
     * The starting value.
     */
    public T zero();
}
