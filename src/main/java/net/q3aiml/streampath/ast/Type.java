package net.q3aiml.streampath.ast;

import java.math.BigDecimal;

/**
 * @author q3aiml
 */
public enum Type {
    STRING(String.class),
    NUMERIC(BigDecimal.class),
    BOOLEAN(Boolean.class),
    ;

    private Class typeClass;

    Type(Class typeClass) {
        this.typeClass = typeClass;
    }

    public Class getTypeClass() {
        return typeClass;
    }
}
