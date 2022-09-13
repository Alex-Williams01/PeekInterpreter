package main.java.Wrapper;
import java.lang.String;

public class Boolean extends Object<java.lang.Boolean> {
    public Boolean(java.lang.Boolean value) {
        super(value);
    }

    public Boolean negate() {
        return new Boolean(!value);
    }

    public Boolean and(Boolean other) {
        return new Boolean(value && other.value);
    }

    public Boolean or(Boolean other) {
        return new Boolean(value || other.value);
    }
    public Boolean exclusiveOr(Boolean other) {
        return new Boolean(value ^ other.value);
    }
    @Override
    public String parseString() {
        return String.valueOf(value);
    }

    @Override
    public java.lang.String toString() {
        return "Boolean{" +
                "value='" + value + '\'' +
                '}';
    }
}
