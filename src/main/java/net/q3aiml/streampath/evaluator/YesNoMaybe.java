package net.q3aiml.streampath.evaluator;

public enum YesNoMaybe {
    YES,
    MAYBE,
    NO,
    ;

    public boolean isYesOrMaybe() {
        return this == YES || this == MAYBE;
    }

    public boolean isYes() {
        return this == YES;
    }

    public boolean isNo() {
        return this == NO;
    }

    public YesNoMaybe and(YesNoMaybe other) {
        if (this.ordinal() > other.ordinal()) {
            return this;
        } else {
            return other;
        }
    }

    public YesNoMaybe and(boolean b) {
        return and(of(b));
    }

    public YesNoMaybe or(YesNoMaybe other) {
        if (this.ordinal() < other.ordinal()) {
            return this;
        } else {
            return other;
        }
    }

    public YesNoMaybe or(boolean b) {
        return or(of(b));
    }

    public static YesNoMaybe of(boolean b) {
        return b ? YES : NO;
    }
}
