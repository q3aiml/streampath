package net.q3aiml.streampath.evaluator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A value that may be available, otherwise can be specified if it will be available in the future.
 * @author ajclayton
 */
public abstract class ContextValue<T> {
    public static <T> ContextValue<T> available(T value) {
        return new AvailableContextValue<T>(value);
    }

    public static <T> ContextValue<T> willBeAvailable(YesNoMaybe value) {
        return new WillBeAvailableContextValue<T>(value);
    }

    /**
     * The value of this context value if it is available.
     * @throws java.lang.IllegalStateException if the value is not available
     * @see #isAvailable()
     */
    public abstract T get();

    /**
     * If this context value is available.
     */
    public abstract  boolean isAvailable();

    /**
     * If value of this context value will be available.
     * @throws java.lang.IllegalStateException if the value is already available
     * @see #isAvailable()
     */
    public abstract YesNoMaybe willBeAvailable();

    public String toString() {
        if (isAvailable()) {
            return "OptionalAwesome.available(" + get() + ")";
        } else {
            return "OptionalAwesome.willBeAvailable(" + willBeAvailable() + ")";
        }
    }

    private static class AvailableContextValue<T> extends ContextValue<T> {
        private final T value;

        private AvailableContextValue(T value) {
            this.value = checkNotNull(value);
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public YesNoMaybe willBeAvailable() {
            throw new IllegalStateException("willBeAvailable(): this OptionalAwesome is already available");
        }
    }

    private static class WillBeAvailableContextValue<T> extends ContextValue<T> {
        private final YesNoMaybe willBeAvailable;

        private WillBeAvailableContextValue(YesNoMaybe willBeAvailable) {
            this.willBeAvailable = checkNotNull(willBeAvailable);
        }

        @Override
        public T get() {
            throw new IllegalStateException("get(): this OptionalAwesome is not available");
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public YesNoMaybe willBeAvailable() {
            return willBeAvailable;
        }
    }
}
