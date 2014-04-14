package net.q3aiml.streampath.ast.aggregate;

/**
 * A strategy for calculating an aggregate measure, such as min or max, over zero or more values.
 * @author q3aiml
 */
public interface Aggregator<T> {
    /**
     * Should be called for every new (non-mapped) value before {@link #add(Object, Object)}.
     * <p/>
     * Does not need to be called for the result of {@link #add(Object, Object)}, which should already be mapped.
     */
    public T map(Object a);

    /**
     * Adds together two (mapped) values and returns a mapped value.
     * @see #map(Object)
     */
    public T add(T a, T b);

    /**
     * The starting (mapped) value.
     */
    public T zero();
}
