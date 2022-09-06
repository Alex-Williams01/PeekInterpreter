package main.java.ASTNodes.Unary;

import main.java.Token.Token;

public class IntegerNode extends UnaryNode {
    private int value;

    public IntegerNode(Token token) {
        this.value = Integer.parseInt(token.data());
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NumberNode{" +
                "value=" + value +
                '}';
    }
}
