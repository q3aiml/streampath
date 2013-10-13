package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.Frame;

/**
 * @author q3aiml
 */
public class SelectRelative extends ValueSelectorNode implements StreamPathNode {
    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new SelectRelative();
    }

    @Override
    public ValueSelectorNode resolve(ValueSelectorNode baseSelector) {
        return baseSelector;
    }

    @Override
    public boolean accepts(Frame frame, FrameContext frameContext) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Object selectSingle(Context context) {
        return context.frame();
    }

    public String toString() {
        return "<relative>";
    }
}
