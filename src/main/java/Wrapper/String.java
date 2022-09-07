package main.java.Wrapper;

public class String extends Object<java.lang.String> {

    public String(java.lang.String value) {
        super(value.substring(1, value.length()-1));
    }

    public String add(Object other) {
        value += other.parseString();
        return this;
    }

    public java.lang.String parseString() {
        return value;
    }

    @Override
    public java.lang.String toString() {
        return "String{" +
                "value='" + value + '\'' +
                '}';
    }
}
