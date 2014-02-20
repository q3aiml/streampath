package net.q3aiml.streampath.ast.selector.value;

import net.q3aiml.streampath.evaluator.Frame;

public interface ExternalReference {
    public boolean references(Frame frame);
}
