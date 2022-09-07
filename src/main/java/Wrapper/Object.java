package main.java.Wrapper;

public abstract class Object<U> {

    protected U value;

    public Object(U value) {
        this.value = value;
    }

    public U getValue() {
        return value;
    };

    public abstract java.lang.String parseString();
}
