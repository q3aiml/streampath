package net.q3aiml.streampath;

import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TestDocuments {
    public static String read(String name) throws IOException {
        return Resources.toString(Resources.getResource(name), StandardCharsets.UTF_8);
    }
}
