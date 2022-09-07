package main.java.Wrapper.Number;

import main.java.Wrapper.Object;

public abstract class Number<T, U> extends Object {
    protected U value;
    public abstract T add(T other);
    public abstract T minus(T other);
    public abstract T divideBy(T other);
    public abstract T multiplyBy(T other);
    public abstract U getValue();
}
