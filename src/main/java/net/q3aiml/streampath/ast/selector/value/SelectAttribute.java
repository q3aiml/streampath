package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.ast.literal.Symbol;
import net.q3aiml.streampath.evaluator.Frame;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

/**
 * @author q3aiml
 */
public class SelectAttribute extends ValueSelectorNode implements StreamPathNode {
    private final QName name;

    public SelectAttribute(ValueSelectorNode parent, QName name) {
        super(parent);
        this.name = name;
    }

    @Override
    protected ValueSelectorNode copy(ValueSelectorNode newParent) {
        return new SelectAttribute(newParent, name);
    }

    @Override
    public boolean accepts(Frame frame, FrameContext frameContext) {
        return frame.attributes().attribute(name) != null;
    }

    @Override
    public Object selectSingle(Context context) {
        final Attribute attribute = ((Frame)parent.selectSingle(context)).attributes().attribute(name);
        return attribute != null ? attribute.getValue() : Symbol.NULL;
    }

    @Override
    public String toString() {
        return parent + "/@" + name;
    }
}
