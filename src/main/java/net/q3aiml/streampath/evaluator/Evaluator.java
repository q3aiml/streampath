package net.q3aiml.streampath.evaluator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.q3aiml.streampath.Document;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.Expressions;
import net.q3aiml.streampath.ast.literal.Literal;
import net.q3aiml.streampath.ast.selector.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.q3aiml.streampath.DocumentSet;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

/**
 * @author q3aiml
 */
public class Evaluator {
    private static final Logger log = LoggerFactory.getLogger(Evaluator.class);

    private final Set<Expression<?, ?>> expressions;
    private final DocumentSet documentSet;

    private Aggregator3000 aggregator3000;

    public Evaluator(DocumentSet documentSet, Set<? extends Expression<?, ?>> expressions) {
        this.expressions = ImmutableSet.copyOf(expressions);
        this.documentSet = documentSet;
        aggregator3000 = new Aggregator3000(expressions, this);
    }

    public EvaluationResult evaluate() throws IOException {
        return evaluate(false);
    }

    public EvaluationResult evaluate(boolean verbose) throws IOException {
        Map<Expression<?, ?>, Object> expressionValues = new HashMap<Expression<?, ?>, Object>();

        for (Document document : referencedDocuments()) {
            Iterable<Frame> frames;
            try {
                frames = new DocumentFrameXMLEventReader().read(document.stream(), document);
            } catch (XMLStreamException e) {
                throw new IOException(e);
            }

            for (Frame frame : frames) {
                aggregator3000.frame(frame);
            }
        }

        for (Expression<?, ?> expression : expressions) {
            Object value = evaluate(null, expression, verbose ? expressionValues : null);
            expressionValues.put(expression, value);
        }

        return new EvaluationResult(expressionValues);
    }

    /*protected*/ Object evaluate(Frame relativeFrame, Expression<?, ?> expression, Map<Expression<?, ?>, Object> verboseResults) {
        Deque<Object> argStack = new LinkedList<Object>();
        Expression<?, ?> previous;

        argStack.push(new ArrayList<Object>());

        Iterator<Expression<?, ?>> depthFirstUnresolvedRulesIterator = Expressions.depthFirstIteration(expression).iterator();
        while (depthFirstUnresolvedRulesIterator.hasNext()) {
            Expression<?, ?> node = depthFirstUnresolvedRulesIterator.next();

            Object result;

            if (node instanceof Selector) {

                // this is a bit hacky, and there's voodoo about what happens if you call with relativeFrame or not, but should work for now
                if (relativeFrame == null) {
                    final Aggregator3000.SelectorAggregateState valueSelectorNodeState = aggregator3000
                            .getSelectorAggregateState((Selector)node);

                    previous = node;
                    while (node != valueSelectorNodeState.aggregatorNode()) {
                        node = depthFirstUnresolvedRulesIterator.next();
                    }
                    result = valueSelectorNodeState.get();

                    if (log.isTraceEnabled()) {
                        log.trace("aggregator... skipping from {} to {} -> " + result, previous, node);
                    }
                } else {
                    ContextValue<Object> frameState = aggregator3000.getFrameState((Selector)node, relativeFrame);
                    if (frameState.isAvailable()) {
                        result = frameState.get();
                    } else {
                        return frameState;
                    }

                }
            } else {
                ImmutableList.Builder<Object> argsBuilder = ImmutableList.builder();
                for (Expression<?,?> argExpression : node.children()) {
                    argsBuilder.add(argStack.pop());
                }
                final ImmutableList<Object> args = argsBuilder.build().reverse();

                try {
                    result = node.apply(args);
                    if (log.isTraceEnabled()) {
                        log.trace("evaluating... {} --[ {} ]>> " + result, args, node);
                    }
                    if (verboseResults != null && !(node instanceof Literal)) {
                        verboseResults.put(node, result);

                    }
                } catch (RuntimeException e) {
                    throw new RuntimeException("failed to process " + node + " with args " + args + ": " + e.getMessage() +
                            "\n\targStack: " + argStack, e);
                }
            }

            argStack.push(result);

            previous = node;
        }

        return argStack.pop();
    }

    private Set<Document> referencedDocuments() {
        return documentSet.documents();
    }
}
