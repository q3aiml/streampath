package net.q3aiml.streampath.ast.selector.value;

import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
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
    public abstract YesNoMaybe accepts(Frame frame, Context context);

    public Set<ExternalReference> externalReferences() {
        final ImmutableSet.Builder<ExternalReference> references = ImmutableSet.builder();
        ValueSelectorNode node = this;
        while (node != null) {
            references.addAll(node.immediateExternalReferences());
            node = node.parent;
        }
        return references.build();
    }

    /**
     * The immediate references (this node, and not any of its descendants) from this node to other data.
     */
    protected Iterable<? extends ExternalReference> immediateExternalReferences() {
        return ImmutableSet.of();
    }

    public ValueSelectorNode resolve(ValueSelectorNode baseSelector) {
        return copy(parent.resolve(baseSelector));
    }

    /**
     * Returns if this selector accepts the {@link Frame}, checking that all ancestors also accept the
     * corresponding ancestor frames.
     * @see #accepts(net.q3aiml.streampath.evaluator.Frame, FrameContext)
     */
    public YesNoMaybe acceptsRecursive(Frame frame, Context context) {
        // check accepts first, as we'll assume that's cheaper that recursing all the way to the root
        YesNoMaybe accepts = accepts(frame, context);
        if (accepts.isNo()) {
            return accepts;
        }

        if (parent == null) {
            return accepts;
        } else {
            return accepts.and(parent.acceptsRecursive(nextFrame(frame), context));
        }
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

    public abstract FrameNavigator select(FrameNavigator frame);
}
