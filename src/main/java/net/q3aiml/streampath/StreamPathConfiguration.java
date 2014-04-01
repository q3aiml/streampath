package net.q3aiml.streampath;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.streampath.ast.DocumentSelectorFactory;
import net.q3aiml.streampath.ast.FunctionFactory;
import net.q3aiml.streampath.ast.aggregate.AggregateModule;
import net.q3aiml.streampath.ast.selector.SelectorsModule;

import java.util.Map;

/**
 * @author ajclayton
 */
public class StreamPathConfiguration {
    private boolean verbose;

    private final ImmutableMap.Builder<String, FunctionFactory> functions =
            ImmutableMap.<String, FunctionFactory>builder();

    private final ImmutableMap.Builder<String, DocumentSelectorFactory> dynamicDocumentSelectors =
            ImmutableMap.<String, DocumentSelectorFactory>builder();

    private StreamPathConfiguration() { }

    public static StreamPathConfiguration defaultConfiguration() {
        StreamPathConfiguration config = emptyConfiguration();
        config.functions.putAll(AggregateModule.defaultFunctionFactories());
        config.dynamicDocumentSelectors.putAll(SelectorsModule.defaultDocumentSelectorFactories());
        return config;
    }

    public static StreamPathConfiguration emptyConfiguration() {
        return new StreamPathConfiguration();
    }

    public boolean isVerbose() {
        return verbose;
    }

    public StreamPathConfiguration verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public StreamPathConfiguration addFunction(String name, FunctionFactory factory) {
        functions.put(name, factory);
        return this;
    }

    public StreamPathConfiguration addDynamicDocumentSelector(String name, DocumentSelectorFactory factory) {
        dynamicDocumentSelectors.put(name, factory);
        return this;
    }

    public Map<String, FunctionFactory> functions() {
        return functions.build();
    }

    public Map<String, DocumentSelectorFactory> dynamicDocumentSelectors() {
        return dynamicDocumentSelectors.build();
    }
}
