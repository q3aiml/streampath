package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import net.q3aiml.streampath.evaluator.Frame;

/**
 * @author q3aiml
 */
public class SelectParent extends ValueSelectorNode implements StreamPathNode {
    public SelectParent(ValueSelectorNode parent) {
        super(parent);
    }

    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new SelectParent(newParent);
    }

    @Override
    public ValueSelectorNode resolve(ValueSelectorNode baseSelector) {
        return parent.resolve(baseSelector.parent);
    }

    @Override
    public YesNoMaybe accepts(Frame frame, Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        return parent.select(frame).parent();
    }

    @Override
    public String toString() {
        return parent + "/..";
    }
}
