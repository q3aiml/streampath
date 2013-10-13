package net.q3aiml.streampath.evaluator;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

/**
 * @author q3aiml
 */
public interface AttributeCollection extends Iterable<Attribute> {

    @Nullable
    public Attribute attribute(QName name);
}
