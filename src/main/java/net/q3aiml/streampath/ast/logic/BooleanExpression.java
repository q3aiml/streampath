package net.q3aiml.streampath.ast.logic;

import net.q3aiml.streampath.ast.Expression;

/**
 * @author q3aiml
 */
/*package*/ abstract class BooleanExpression<I> implements Expression<Boolean> {
    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Override
    public String toVerboseString() {
        return toString();
    }
}
