package net.q3aiml.streampath.lang;

import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;

/**
 * @author q3aiml
 */
@BuildParseTree
@SuppressWarnings("InfiniteRecursion")
public class Parser extends ParserBase {

    public Rule Expression() {
        return Sequence(
                BooleanExpression(),
                EOI
        );
    }

    Rule BooleanExpression() {
        return Sequence(
                UnaryBooleanExpression(),
                ZeroOrMore(
                        FirstOf(AND, OR),
                        UnaryBooleanExpression()
                )
        );
    }

    Rule UnaryBooleanExpression() {
        return FirstOf(
                Sequence(
                        NOT,
                        EqualityExpression()
                ),
                EqualityExpression()
        );
    }

    // == and !=
    Rule EqualityExpression() {
        return Sequence(
                RelationalExpression(),
                ZeroOrMore(
                        FirstOf(EQUAL, NOTEQUAL),
                        RelationalExpression()
                )
        );
    }

    // >, <, ...
    Rule RelationalExpression() {
        return Sequence(
                AdditiveExpression(),
                ZeroOrMore(
                        FirstOf(LE, GE, LT, GT),
                        AdditiveExpression()
                )
        );
    }

    // + -
    Rule AdditiveExpression() {
        return Sequence(
                MultiplicativeExpression(),
                ZeroOrMore(
                        FirstOf(PLUS, MINUS),
                        MultiplicativeExpression()
                )
        );
    }

    Rule MultiplicativeExpression() {
        return Sequence(
                Term(),
                ZeroOrMore(
                        FirstOf(STAR, SLASH),
                        Term()
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
        return Sequence(
                FirstOf(
                        Terminal("min"),
                        Terminal("max"),
                        Terminal("sum"),
                        Terminal("count")
                ),
                Terminal("("),
                FirstOf(
                        Sequence(
                                AdditiveExpression(),
                                ZeroOrMore(
                                        Terminal(","),
                                        AdditiveExpression()
                                )
                        ),
                        EMPTY)
                ),
                Terminal(")")
        );
    }

    Rule Selector() {
        return Sequence(
                FirstOf(
                        Sequence(
                                DocumentSelector(),
                                Terminal("::")
                        ),
                        EMPTY
                ),
                ValueSelector(),
                Spacing()
        );
    }

    Rule DocumentSelector() {
        return Sequence(
                OneOrMore(LetterOrDigit()),
                Optional(
                        '(',
                        FirstOf(
                                Sequence(
                                        Literal(),
                                        ZeroOrMore(
                                                Terminal(","),
                                                Literal()
                                        )
                                ),
                                EMPTY
                        ),
                        ')'
                )
        );
    }

    Rule ValueSelector() {
        return FirstOf(
                Sequence(
                        "//",
                        ValueStep()
                ),
                Sequence(
                        '/',
                        Optional(ValueStep())
                ),
                ValueStep()
        );
    }

    Rule ValueStep() {
        return Sequence(
                SelectSomething(),
                ZeroOrMore(
                        FirstOf(
                                Sequence(
                                        "//",
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
                OneOrMore(LetterOrDigit())
        );
    }

    @SuppressSubnodes
    Rule SelectChildren() {
        return Sequence(
                OneOrMore(LetterOrDigit()),
                Optional(ValueSelectorPredicate())
        );
    }

    @SuppressSubnodes
    Rule SelectParent() {
        return Terminal("..");
    }

    Rule ValueSelectorPredicate() {
        return Sequence(
                Terminal("["),
                BooleanExpression(),
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
