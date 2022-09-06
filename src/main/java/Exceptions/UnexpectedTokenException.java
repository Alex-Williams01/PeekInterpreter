package main.java.Exceptions;

public class UnexpectedTokenException extends RuntimeException {
    public UnexpectedTokenException(String errorMessage) {
        super(errorMessage);
    }
}