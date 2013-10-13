package net.q3aiml.streampath.lang;

import net.q3aiml.streampath.ast.Expression;
import net.q3aiml.streampath.ast.arithmetic.ArithmeticExpression;
import net.q3aiml.streampath.ast.literal.Literal;
import net.q3aiml.streampath.ast.logic.BinaryBooleanExpression;
import net.q3aiml.streampath.ast.logic.EqualityExpression;
import net.q3aiml.streampath.ast.logic.NumericComparisonExpression;
import net.q3aiml.streampath.ast.logic.UnaryBooleanExpression;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.common.Factory;
import org.parboiled.support.Var;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author q3aiml
 */
@BuildParseTree
@SuppressWarnings("InfiniteRecursion")
public class Parser extends ParserBase<Expression<?, ?>> {

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
