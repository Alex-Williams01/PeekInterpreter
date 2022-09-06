package main.java.Token;

import java.util.ArrayList;
import java.util.List;

public class TokenisedLine {
    private final List<Token> tokens = new ArrayList<>();
    private final int lineNumber;

    public TokenisedLine(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "TokenList {tokens: %s, lineNumber: %s}".formatted(tokens, lineNumber);
    }
}
