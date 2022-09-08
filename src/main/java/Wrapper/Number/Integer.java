package main.java.Wrapper.Number;
import java.lang.String;

public class Integer extends Number<Integer, java.lang.Integer> {

    public Integer(int value) {
        super(value);
    }

    public Integer add(Integer other) {
        return new Integer(value + other.value);
    }

    public Integer minus(Integer other) {
        return new Integer(value - other.value);
    }

    public Integer multiplyBy(Integer other) {
        return new Integer(value * other.value);
    }

    public Integer divideBy(Integer other) {
        if (other.value == 0) {
            //TODO replace with custom class
            throw new ArithmeticException("Divide by zero");
        }
        return new Integer(value / other.value);
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