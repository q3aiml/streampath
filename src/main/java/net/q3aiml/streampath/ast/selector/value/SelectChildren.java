package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import net.q3aiml.streampath.evaluator.Frame;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author q3aiml
 */
public class SelectChildren extends ValueSelectorNode implements StreamPathNode {
    private final String match;

    public SelectChildren(ValueSelectorNode parent, String match) {
        super(parent);
        this.match = checkNotNull(match, "match must not be null");
    }

    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new SelectChildren(newParent, match);
    }

    @Override
    public YesNoMaybe accepts(Frame frame, Context context) {
        if (frame.isRoot()) {
            return YesNoMaybe.NO;
        }
        return YesNoMaybe.of(match.equals(frame.name()));
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        return parent.select(frame).child(match);
    }

    @Override
    public String toString() {
        return parent + "/" + match;
    }
}
