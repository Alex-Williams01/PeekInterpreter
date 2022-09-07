package main.java.Wrapper.Number;
import java.lang.String;

public class Double extends Number<Double, java.lang.Double> {

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

    public java.lang.Double getValue() {
        return value;
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
