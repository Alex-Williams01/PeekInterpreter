package main.java.Wrapper.Number;
import java.lang.String;

public class Integer extends Number<Integer, java.lang.Integer> {

    public Integer(int value) {
        super(value);
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
        if (other.value == 0) {
            //TODO replace with custom class
            throw new ArithmeticException("Divide by zero");
        }
        value /= other.value;
        return this;
    }

    public Integer multiplyBy(Integer other) {
        value *= other.value;
        return this;
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
