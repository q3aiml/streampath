package net.q3aiml.streampath.ast.selector.value;

import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.evaluator.Frame;

import java.util.Set;

/**
 * @author q3aiml
 */
public abstract class ValueSelectorNode {
    protected final ValueSelectorNode parent;

    protected ValueSelectorNode() {
        this.parent = null;
    }

    protected ValueSelectorNode(ValueSelectorNode parent) {
        this.parent = parent;
    }

    protected abstract ValueSelectorNode copy(ValueSelectorNode newParent);

    public ValueSelectorNode parent() {
        return parent;
    }

    /**
     * Returns if this selector accepts the {@link net.q3aiml.streampath.evaluator.Frame}, assuming each ancestor of this selector accepts each
     * respective ancestor frame.
     * @see #acceptsRecursive(net.q3aiml.streampath.evaluator.Frame, FrameContext)
     */
    public abstract boolean accepts(Frame frame, FrameContext frameContext);

    public ValueSelectorNode resolve(ValueSelectorNode baseSelector) {
        return copy(parent.resolve(baseSelector));
    }

    /**
     * Returns if this selector accepts the {@link Frame}, checking that all ancestors also accept the
     * corresponding ancestor frames.
     * @see #accepts(net.q3aiml.streampath.evaluator.Frame, FrameContext)
     */
    public boolean acceptsRecursive(Frame frame, FrameContext frameContext) {
        // check accepts first, as we'll assume that's cheaper that recursing all the way to the root
        return accepts(frame, frameContext)
                && (parent == null || parent.acceptsRecursive(nextFrame(frame), frameContext));
    }

    private Frame nextFrame(Frame frame) {
        if (consumesFrame()) {
            return frame.parent();
        } else {
            return frame;
        }
    }

    public boolean consumesFrame() {
        return true;
    }

    public abstract Object selectSingle(Context context);
}
