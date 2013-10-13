package net.q3aiml.streampath.ast.selector;

import com.google.common.collect.ImmutableList;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Expression;

import java.util.List;

/**
 * @author q3aiml
 */
public class Selector implements Expression<Object, Object>, SelectorBase {
    private final DocumentSelector documentSelector;
    private final ValueSelector valueSelector;

    public Selector(DocumentSelector documentSelector, ValueSelector valueSelector) {
        this.documentSelector = documentSelector;
        this.valueSelector = valueSelector;
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
    public Object getValue(Context context) {
        return valueSelector.selectSingle(context);
    }

    @Override
    public String toString() {
        return (documentSelector != null ? documentSelector + "::" : "" ) + valueSelector;
    }
}