package net.q3aiml.streampath.ast.selector;

import net.q3aiml.streampath.ast.selector.value.SelectAttribute;
import net.q3aiml.streampath.ast.selector.value.SelectRelative;
import net.q3aiml.streampath.ast.selector.value.ValueSelectorNode;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author q3aiml
 */
public class ValueSelector implements SelectorBase {
    private ValueSelectorNode selector;

    public ValueSelector(ValueSelectorNode selector) {
        this.selector = checkNotNull(selector, "selector must not be null");
    }

    @Override
    public String toString() {
        return selector.toString();
    }

    public ValueSelectorNode selector() {
        return selector;
    }

    public boolean isExternal() {
        return !(selector instanceof SelectAttribute && selector.parent() instanceof SelectRelative);
    }
}
