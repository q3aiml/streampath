package net.q3aiml.streampath.ast;

import java.util.List;

/**
 * @author q3aiml
 */
public interface FunctionFactory {
    public Expression<?> create(List<Expression<?>> arguments);
}
