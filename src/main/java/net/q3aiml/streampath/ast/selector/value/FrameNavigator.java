package net.q3aiml.streampath.ast.selector.value;

import javax.xml.namespace.QName;

/**
 * @author ajclayton
 */
public interface FrameNavigator {
    FrameNavigator attribute(QName name);

    FrameNavigator child(String childName);

    FrameNavigator parent();
}
