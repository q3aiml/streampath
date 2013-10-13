package net.q3aiml.streampath.ast.literal;

import net.q3aiml.streampath.ast.Context;
import net.q3aiml.streampath.ast.Keywords;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author q3aiml
 */
public class SymbolLiteral extends Literal<Symbol> {
    public Symbol symbol;

    public SymbolLiteral(Symbol symbol) {
        this.symbol = symbol;
    }

    public SymbolLiteral(String symbol) {
        this(Keywords.findByRepresentation(symbol, Symbol.values()));
    }

    @Override
    public Class<Symbol> getValueType() {
        return Symbol.class;
    }

    @Override
    public Symbol apply(List<Object> arguments) {
        checkArgument(arguments.size() == 0, "expected no arguments, not %s", arguments.size());
        return symbol;
    }

    @Override
    public Symbol getValue(Context context) {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol.representation();
    }

}
