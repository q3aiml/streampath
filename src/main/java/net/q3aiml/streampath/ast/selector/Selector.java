package net.q3aiml.streampath.ast.selector;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Expression;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author q3aiml
 */
public class Selector implements Expression<Object>, SelectorBase {
    private final DocumentSelector documentSelector;
    private final ValueSelector valueSelector;

    public Selector(DocumentSelector documentSelector, ValueSelector valueSelector) {
        this.documentSelector = checkNotNull(documentSelector, "documentSelector must not be null");
        this.valueSelector = checkNotNull(valueSelector, "valueSelector must not be null");
    }

    @Override
    public Expression<Object> copy(List<Expression<?>> children) {
        throw new UnsupportedOperationException("cannot copy a selector");
    }

    @Override
    public List<? extends Expression> children() {
        return ImmutableList.of();
    }

    public DocumentSelector getDocumentSelector() {
        return documentSelector;
    }

    public ValueSelector getValueSelector() {
        return valueSelector;
    }

    @Override
    public Class<Object> getValueType() {
        return Object.class;
    }

    @Override
    public Object apply(List<Object> arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String toVerboseString() {
        return toString();
    }

    @Override
    public String toString() {
        return (documentSelector != null ? documentSelector + "::" : "" ) + valueSelector;
    }
}
