package main.java.Wrapper;

public abstract class Number<T> extends Object {
    public abstract T add(T other);
    public abstract T minus(T other);
    public abstract T divideBy(T other);
    public abstract T multiplyBy(T other);
}
