package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.literal.Symbol;
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
    public boolean accepts(Frame frame, FrameContext frameContext) {
        return !frame.isRoot() && match.equals(frame.name());
    }

    @Override
    public Object selectSingle(Context context) {
        throw new UnsupportedOperationException(toString());
    }

    @Override
    public String toString() {
        return parent + "/" + match;
    }
}
