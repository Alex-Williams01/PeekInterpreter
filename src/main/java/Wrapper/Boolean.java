package main.java.Wrapper;
import java.lang.String;

public class Boolean extends Object<java.lang.Boolean> {
    public Boolean(java.lang.Boolean value) {
        super(value);
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
