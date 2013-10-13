package net.q3aiml.streampath.ast.literal;

import net.q3aiml.streampath.ast.Keyword;

/**
* @author q3aiml
*/
public enum Symbol implements Keyword {
    NULL("null"),
    ;

    private String representation;

    Symbol(String representation) {
        this.representation = representation;
    }

    @Override
    public String representation() {
        return representation;
    }

    public String toString() {
        return representation;
    }
}
