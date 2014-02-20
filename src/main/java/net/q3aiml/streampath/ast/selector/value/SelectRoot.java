package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import net.q3aiml.streampath.evaluator.Frame;

/**
 * @author q3aiml
 */
public class SelectRoot extends ValueSelectorNode implements StreamPathNode {
    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new SelectRoot();
    }

    @Override
    public ValueSelectorNode resolve(ValueSelectorNode baseSelector) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public YesNoMaybe accepts(Frame frame, Context context) {
        return YesNoMaybe.of(frame.parent() == null);
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "<root>";
    }
}
