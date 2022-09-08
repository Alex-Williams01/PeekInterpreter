package main.java.Wrapper.Number;

import main.java.Wrapper.Boolean;
import main.java.Wrapper.Object;

public abstract class Number<T, U> extends Object<U> {
    public Number(U value) {
        super(value);
    }
    public abstract T add(T other);
    public abstract T minus(T other);
    public abstract T divideBy(T other);
    public abstract T multiplyBy(T other);

    public abstract Boolean lessThan(T other);
    public abstract Boolean lessThanEqual(T other);
    public abstract Boolean greaterThan(T other);
    public abstract Boolean greaterThanEqual(T other);
}
