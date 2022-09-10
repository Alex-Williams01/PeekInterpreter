package main.java.ASTNodes.Unary;

import main.java.Token.Token;

public class BooleanNode extends UnaryNode {
    private boolean value;

    public BooleanNode(Token token) {
        this.value = Boolean.parseBoolean(token.data());
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BooleanNode{" +
                "value=" + value +
                '}';
    }
}