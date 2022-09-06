package main.java.Exceptions;

public class MissingTokenException extends RuntimeException {
    public MissingTokenException(String errorMessage) {
        super(errorMessage);
    }
}
