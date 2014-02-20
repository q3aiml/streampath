package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
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
    public YesNoMaybe accepts(Frame frame, Context context) {
        throw new UnsupportedOperationException("TODO!");
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        return frame;
    }

    public String toString() {
        return "<relative>";
    }
}
