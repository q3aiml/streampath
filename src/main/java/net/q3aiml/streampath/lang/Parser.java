package net.q3aiml.streampath.lang;

import net.q3aiml.streampath.StreamPathConfiguration;
import net.q3aiml.streampath.ast.DocumentSelectorFactory;
import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.FunctionFactory;
import net.q3aiml.streampath.ast.arithmetic.ArithmeticExpression;
import net.q3aiml.streampath.ast.literal.Literal;
import net.q3aiml.streampath.ast.logic.BinaryBooleanExpression;
import net.q3aiml.streampath.ast.logic.EqualityExpression;
import net.q3aiml.streampath.ast.logic.NumericComparisonExpression;
import net.q3aiml.streampath.ast.logic.UnaryBooleanExpression;
import net.q3aiml.streampath.ast.selector.DocumentSelector;
import net.q3aiml.streampath.ast.selector.IdentifierDocumentSelector;
import net.q3aiml.streampath.ast.selector.Selector;
import net.q3aiml.streampath.ast.selector.ValueSelector;
import net.q3aiml.streampath.ast.selector.value.*;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.Factory;
import org.parboiled.support.Var;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author q3aiml
 */
@BuildParseTree
@SuppressWarnings("InfiniteRecursion")
public class Parser extends ParserBase<Expression<?>> {
    private final Map<String, FunctionFactory> functions;
    private final Map<String, DocumentSelectorFactory> dynamicDocumentSelectors;

    public Parser() {
        this(StreamPathConfiguration.defaultConfiguration());
    }

    public Parser(StreamPathConfiguration configuration) {
        functions = configuration.functions();
        dynamicDocumentSelectors = configuration.dynamicDocumentSelectors();
    }

    public Rule Expression() {
        return Sequence(
                BooleanExpression(),
                EOI
        );
    }

    Rule BooleanExpression() {
        Var<String> op = new Var<String>();
        return Sequence(
                UnaryBooleanExpression(),
                ZeroOrMore(
                        FirstOf(AND, OR), op.set(match()),
                        UnaryBooleanExpression(),
                        push(new BinaryBooleanExpression(op.get().trim(), (Expression)pop(1), (Expression)pop()))
                )
        );
    }

    Rule UnaryBooleanExpression() {
        Var<String> op = new Var<String>();
        return FirstOf(
                Sequence(
                        NOT, op.set(match()),
                        EqualityExpression(),
                        push(new UnaryBooleanExpression(op.get().trim(), (Expression)pop()))
                ),
                EqualityExpression()
        );
    }

    // == and !=
    Rule EqualityExpression() {
        Var<String> op = new Var<String>();
        return Sequence(
                RelationalExpression(),
                ZeroOrMore(
                        FirstOf(EQUAL, NOTEQUAL), op.set(match()),
                        RelationalExpression(),
                        push(new EqualityExpression(op.get().trim(), (Expression)pop(1), (Expression)pop()))
                )
        );
    }

    // >, <, ...
    Rule RelationalExpression() {
        Var<String> op = new Var<String>();
        return Sequence(
                AdditiveExpression(),
                ZeroOrMore(
                        FirstOf(LE, GE, LT, GT), op.set(match()),
                        AdditiveExpression(),
                        push(new NumericComparisonExpression(op.get().trim(), (Expression)pop(1), (Expression)pop()))
                )
        );
    }

    // + -
    Rule AdditiveExpression() {
        Var<String> op = new Var<String>();
        return Sequence(
                MultiplicativeExpression(),
                ZeroOrMore(
                        FirstOf(PLUS, MINUS), op.set(match()),
                        MultiplicativeExpression(),
                        push(new ArithmeticExpression(op.get().trim(), (Expression)pop(1), (Expression)pop()))
                )
        );
    }

    Rule MultiplicativeExpression() {
        Var<String> op = new Var<String>();
        return Sequence(
                Term(),
                ZeroOrMore(
                        FirstOf(STAR, SLASH), op.set(match()),
                        Term(),
                        push(new ArithmeticExpression(op.get().trim(), (Expression)pop(1), (Expression)pop()))
                )
        );
    }

    Rule Term() {
        return FirstOf(
                Literal(),
                Function(),
                Selector()
        );
    }

    Rule Function() {
        Var<String> op = new Var<String>();
        Var<List<Expression<?>>> args = new Var<List<Expression<?>>>(new Factory<List<Expression<?>>>() {
            @Override
            public List<Expression<?>> create() {
                return new ArrayList<Expression<?>>();
            }
        });
        return Sequence(
                FirstOf(
                        Terminal("min"),
                        Terminal("max"),
                        Terminal("sum"),
                        Terminal("count")
                ), op.set(match()),
                Terminal("("),
                FirstOf(
                        Sequence(
                                AdditiveExpression(), args.get().add((Expression)pop()),
                                ZeroOrMore(
                                        Terminal(","),
                                        AdditiveExpression(), args.get().add((Expression)pop())
                                )
                        ),
                        EMPTY
                ),
                Terminal(")"),
                push(createFunction(op.get().trim(), args.get()))
        );
    }

    protected Expression<?> createFunction(String name, List<Expression<?>> arguments) {
        final FunctionFactory functionFactory = functions.get(name);
        if (functionFactory == null) {
            throw new IllegalArgumentException("unknown function '" + name + "'"
                    + ", valid functions: " + functions.keySet());
        }
        return functionFactory.create(arguments);
    }

    Rule Selector() {
        return Sequence(
                FirstOf(
                        Sequence(
                                DocumentSelector(),
                                Terminal("::")
                        ),
                        Sequence(
                            EMPTY,
                            push(new IdentifierDocumentSelector())
                        )
                ),
                ValueSelector(),
                push(new Selector((DocumentSelector)pop(1), (ValueSelector)pop())),
                Spacing()
        );
    }

    Rule DocumentSelector() {
        Var<String> selector = new Var<String>();
        Var<List<Literal>> args = new Var<List<Literal>>(new Factory<List<Literal>>() {
            @Override
            public List<Literal> create() {
                return new ArrayList<Literal>();
            }
        });
        return Sequence(
                OneOrMore(LetterOrDigit()), selector.set(match()),
                Optional(
                        '(',
                        FirstOf(
                                Sequence(
                                        Literal(), args.get().add((Literal)pop()),
                                        ZeroOrMore(
                                                Terminal(","),
                                                Literal(), args.get().add((Literal)pop())
                                        )
                                ),
                                EMPTY
                        ),
                        ')'
                ),
                push(createDocumentSelector(selector.get(), args.get()))
        );
    }

    protected DocumentSelector createDocumentSelector(String name, List<Literal> arguments) {
        final DocumentSelectorFactory documentSelectorFactory = dynamicDocumentSelectors.get(name);
        if (documentSelectorFactory == null) {
            throw new IllegalArgumentException("unknown document selector '" + name + "'"
                    + ", valid selectors: " + functions.keySet());
        }
        return documentSelectorFactory.create(arguments);
    }

    Rule ValueSelector() {
        return Sequence(
                FirstOf(
                        Sequence(
                                "//",
                                push(new DescendantOrSelf(new SelectRoot())),
                                ValueStep()
                        ),
                        Sequence(
                                '/',
                                push(new SelectRoot()),
                                Optional(ValueStep())
                        ),
                        Sequence(
                                push(new SelectRelative()),
                                ValueStep()
                        )
                ),
                push(new ValueSelector((ValueSelectorNode)pop()))
        );
    }

    Rule ValueStep() {
        return Sequence(
                SelectSomething(),
                ZeroOrMore(
                        FirstOf(
                                Sequence(
                                        "//",
                                        push(new DescendantOrSelf((ValueSelectorNode)pop())),
                                        SelectSomething()
                                ),
                                Sequence(
                                        '/',
                                        SelectSomething()
                                )
                        )
                )
        );
    }

    Rule SelectSomething() {
        return FirstOf(
                SelectAttribute(),
                SelectChildren(),
                SelectParent()
        );
    }

    @SuppressSubnodes
    Rule SelectAttribute() {
        return Sequence(
                '@',
                OneOrMore(LetterOrDigit()),
                push(new SelectAttribute((ValueSelectorNode)pop(), QName.valueOf(match())))
        );
    }

    @SuppressSubnodes
    Rule SelectChildren() {
        return Sequence(
                OneOrMore(LetterOrDigit()),
                push(new SelectChildren((ValueSelectorNode)pop(), match())),
                Optional(ValueSelectorPredicate())
        );
    }

    @SuppressSubnodes
    Rule SelectParent() {
        return Sequence(
                "..",
                push(new SelectParent((ValueSelectorNode)pop()))
        );
    }

    Rule ValueSelectorPredicate() {
        return Sequence(
                Terminal("["),
                BooleanExpression(),
                push(PredicateFilter.matching((ValueSelectorNode)pop(1), (Expression)pop())),
                ']'
        );
    }


    final Rule EQUAL = Terminal("=="); // TODO =??
    final Rule NOTEQUAL = Terminal("!=");
    final Rule GT = Terminal(">", AnyOf("=>"));
    final Rule LT = Terminal("<", AnyOf("=<"));
    final Rule GE = Terminal(">=");
    final Rule LE = Terminal("<=");

    final Rule NOT = Terminal("not");
    final Rule OR = Terminal("or");
    final Rule AND = Terminal("and");

    final Rule PLUS = Terminal("+");
    final Rule MINUS = Terminal("-");
    final Rule STAR = Terminal("*");
    final Rule SLASH = Terminal("/");
}
