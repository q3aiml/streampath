package net.q3aiml.streampath.ast.aggregate;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.FunctionFactory;

import java.util.List;
import java.util.Map;

public class AggregateModule {
    private static final ImmutableMap<String, FunctionFactory> FUNCTION_FACTORIES = ImmutableMap.of(
            "count", new FunctionFactory() {
                @Override
                public Expression<?> create(List<Expression<?>> arguments) {
                    return NodeSetAggregateFunction.count(arguments);
                }
            },
            "min", new FunctionFactory() {
                @Override
                public Expression<?> create(List<Expression<?>> arguments) {
                    return NumericAggregateFunction.min(arguments);
                }
            },
            "max", new FunctionFactory() {
                @Override
                public Expression<?> create(List<Expression<?>> arguments) {
                    return NumericAggregateFunction.max(arguments);
                }
            },
            "sum", new FunctionFactory() {
                @Override
                public Expression<?> create(List<Expression<?>> arguments) {
                    return NumericAggregateFunction.sum(arguments);
                }
            }
    );

    public static Map<String, FunctionFactory> defaultFunctionFactories() {
        return FUNCTION_FACTORIES;
    }
}
