package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.Frame;
import net.q3aiml.streampath.ast.Context;

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
    public boolean accepts(Frame frame, FrameContext frameContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean acceptsRecursive(Frame frame, FrameContext frameContext) {
        boolean acceptsRecursive = false;
        while (frame != null) {
            acceptsRecursive |= parent.acceptsRecursive(frame, frameContext);
            frame = frame.parent();
        }
        return acceptsRecursive;
    }

    @Override
    public Object selectSingle(Context context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return parent + "//";
    }
}
