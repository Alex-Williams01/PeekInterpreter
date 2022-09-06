package main.java.Wrapper;
import java.lang.String;

public class Double extends Number<Double> {
    private double value;

    public Double(double value) {
        this.value = value;
    }

    public Double add(Double other) {
        value += other.value;
        return this;
    }

    public Double minus(Double other) {
        value -= other.value;
        return this;
    }

    public Double divideBy(Double other) {
        value /= other.value;
        return this;
    }

    public Double multiplyBy(Double other) {
        value *= other.value;
        return this;
    }

    public double getValue() {
        return value;
    }

    public String parseString() {
        return String.valueOf(value);
    }
}
