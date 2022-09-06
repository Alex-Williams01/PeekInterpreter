package main.java.Wrapper;

public class String extends Object {
    private java.lang.String value;

    public String(java.lang.String value) {
        this.value = value.substring(1, value.length()-1);;
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
