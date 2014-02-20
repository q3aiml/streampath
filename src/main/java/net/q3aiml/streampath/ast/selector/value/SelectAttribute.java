package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Context;
import net.q3aiml.streampath.ast.StreamPathNode;
import net.q3aiml.streampath.evaluator.YesNoMaybe;
import net.q3aiml.streampath.evaluator.Frame;

import javax.xml.namespace.QName;

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
    public YesNoMaybe accepts(Frame frame, Context context) {
        return YesNoMaybe.of(frame.attributes().attribute(name) != null);
    }

    @Override
    public FrameNavigator select(FrameNavigator frame) {
        return parent.select(frame).attribute(name);
    }

    @Override
    public String toString() {
        return parent + "/@" + name;
    }
}
