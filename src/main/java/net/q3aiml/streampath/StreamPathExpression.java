package net.q3aiml.streampath;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.streampath.ast.Expression;
import org.parboiled.Node;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A compiled StreamPath expression.
 * @author ajclayton
 */
public class StreamPathExpression {
    private final String originalExpressionString;
    private final ParsingResult<Expression<?>> parsingResult;
    private final Expression<?> expression;

    /*package*/ StreamPathExpression(String originalExpressionString, ParsingResult<Expression<?>> parsingResult) {
        checkArgument(!parsingResult.hasErrors(), "parsingResult must not have errors");
        this.originalExpressionString = originalExpressionString;
        this.parsingResult = parsingResult;
        this.expression = parsingResult.parseTreeRoot.getValue();
    }

    /**
     * The original string expression that was parsed.
     */
    public String originalExpression() {
        return originalExpressionString;
    }

    /**
     * The parsed expression.
     */
    public Expression<?> expression() {
        return expression;
    }

    /*package*/ Map<Expression<?>, String> originalExpressionMap() {
        ImmutableMap.Builder<Expression<?>, String> originalTextMap = ImmutableMap.builder();
        originalText(parsingResult.parseTreeRoot, originalTextMap);
        return originalTextMap.build();
    }

    private void originalText(Node<Expression<?>> expressionNode,
                              ImmutableMap.Builder<Expression<?>, String> originalTextMap)
    {
        String originalText = ParseTreeUtils.getNodeText(expressionNode, parsingResult.inputBuffer);
        originalTextMap.put(expressionNode.getValue(), originalText);

        for (Node<Expression<?>> child : expressionNode.getChildren()) {
            originalText(child, originalTextMap);
        }
    }

    private String findOriginalText(Node<Expression<?>> searchIn, Expression<?> toFind) {
        if (searchIn.getValue() == toFind) {
            return ParseTreeUtils.getNodeText(searchIn, parsingResult.inputBuffer);
        }

        for (Node<Expression<?>> child : searchIn.getChildren()) {
            String originalText = findOriginalText(child, toFind);
            if (originalText != null) {
                return originalText;
            }
        }

        return null;
    }
}
