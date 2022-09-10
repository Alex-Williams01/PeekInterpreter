package main.java.Wrapper.Number;
import main.java.Wrapper.Boolean;

import java.lang.String;

public class Double extends Number<Double, java.lang.Double> {

    public Double(double value) {
        super(value);
    }

    public Double add(Double other) {
        return new Double(value + other.value);
    }

    public Double minus(Double other) {
        return new Double(value - other.value);
    }

    public Double divideBy(Double other) {
        return new Double(value / other.value);
    }

    public Double multiplyBy(Double other) {
        return new Double(value * other.value);
    }

    public Double negated() {
        return new Double(value * -1);
    }

    @Override
    public Boolean lessThan(Double other) {
        return new Boolean(value < other.value);
    }

    @Override
    public Boolean lessThanEqual(Double other) {
        return new Boolean(value <= other.value);
    }

    @Override
    public Boolean greaterThan(Double other) {
        return new Boolean(value > other.value);
    }

    @Override
    public Boolean greaterThanEqual(Double other) {
        return new Boolean(value >= other.value);
    }

    public String parseString() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return "Double{" +
                "value=" + value +
                '}';
    }
}
