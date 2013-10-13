package net.q3aiml.streampath.ast.selector.value;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.evaluator.Frame;
import net.q3aiml.streampath.ast.Context;

import java.util.Set;

/**
 * @author q3aiml
 */
public class FrameContext implements Context {
    private final Frame frame;

    public FrameContext(Frame frame) {
        this.frame = frame;
    }

    @Override
    public Frame frame() {
        return frame;
    }
}
