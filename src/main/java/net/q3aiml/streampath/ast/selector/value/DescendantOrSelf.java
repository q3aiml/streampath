package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import net.q3aiml.streampath.evaluator.Frame;
import net.q3aiml.streampath.evaluator.Context;

/**
 * descendant-or-self aka //
 * @author q3aiml
 */
public class DescendantOrSelf extends ValueSelectorNode implements StreamPathNode {
    public DescendantOrSelf(ValueSelectorNode parent) {
        super(parent);
    }

    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new DescendantOrSelf(newParent);
    }

    @Override
    public YesNoMaybe accepts(Frame frame, Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public YesNoMaybe acceptsRecursive(Frame frame, Context context) {
        YesNoMaybe acceptsRecursive = YesNoMaybe.NO;
        while (frame != null) {
            acceptsRecursive = acceptsRecursive.or(parent.acceptsRecursive(frame, context));
            frame = frame.parent();
        }
        return acceptsRecursive;
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return parent + "//";
    }
}
