package net.q3aiml.streampath.lang;

import net.q3aiml.streampath.ast.literal.*;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import net.q3aiml.streampath.ast.StreamPathNode;

/**
 * @author q3aiml
 */
public class ParserBase<T extends StreamPathNode> extends BaseParser<StreamPathNode> {

    Rule Literal() {
        return Sequence(
                FirstOf(
                        NumberLiteral(),
                        StringLiteral(),
                        BooleanLiteral(),
                        NullLiteral()
                ),
                Spacing()
        );
    }

    Rule NullLiteral() {
        return Sequence("null", TestNot(LetterOrDigit()), push(new SymbolLiteral(Symbol.NULL)));
    }

    @SuppressSubnodes
    Rule BooleanLiteral() {
        return FirstOf(
                Sequence("true", TestNot(LetterOrDigit()), push(new BooleanLiteral(true))),
                Sequence("false", TestNot(LetterOrDigit()), push(new BooleanLiteral(false)))
        );
    }

    @SuppressSubnodes
    Rule StringLiteral() {
        return Sequence(
                '"',
                ZeroOrMore(
                        FirstOf(
                                Escape(),
                                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                push(new StringLiteral(match())),
                '"'
        );
    }

    Rule Escape() {
        return Sequence('\\', AnyOf("btnfr\"\\"));
    }

    @SuppressSubnodes
    Rule NumberLiteral() {
        return Sequence(
                FirstOf(
                        Sequence(OneOrMore(Digit()), '.', ZeroOrMore(Digit())),
                        Sequence('.', OneOrMore(Digit())),
                        OneOrMore(Digit())
                ),
                push(new NumberLiteral(match()))
        );
    }

    @MemoMismatches
    Rule LetterOrDigit() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), Digit(), '_', '$');
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressNode
    @DontLabel
    Rule Terminal(String string) {
        return Sequence(string, Spacing()).label('\'' + string + '\'');
    }

    @SuppressNode
    @DontLabel
    Rule Terminal(String string, Rule mustNotFollow) {
        return Sequence(string, TestNot(mustNotFollow), Spacing()).label('\'' + string + '\'');
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

                // comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/")
        ));
    }
}
