package main.java.Wrapper.Number;
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
