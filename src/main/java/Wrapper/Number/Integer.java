package main.java.Wrapper.Number;
import java.lang.String;

public class Integer extends Number<Integer, java.lang.Integer> {

    public Integer(int value) {
        this.value = value;
    }

    public Integer add(Integer other) {
        value += other.value;
        return this;
    }

    public Integer minus(Integer other) {
        value -= other.value;
        return this;
    }

    public Integer divideBy(Integer other) {
        value /= other.value;
        return this;
    }

    public Integer multiplyBy(Integer other) {
        value *= other.value;
        return this;
    }

    public java.lang.Integer getValue() {
        return value;
    }

    public String parseString() {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return "Integer{" +
                "value=" + value +
                '}';
    }
}
