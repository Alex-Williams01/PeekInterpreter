package main.java.Wrapper;

public class String extends Object<java.lang.String> {

    public String(java.lang.String value) {
        super(value
                .replaceAll("^\"", "")
                .replaceAll("\"$", ""));
    }

    public String add(Object other) {
        return new String(value+other.parseString());
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
