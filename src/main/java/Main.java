package main.java;

import main.java.Lexer.Lexer;
import main.java.Parser.Parser;

public class Main {
    public static void main(String[] args) {
        Lexer lexer = new Lexer(args[0]);
        var parser = new Parser(lexer);
        parser.start();
    }
}
