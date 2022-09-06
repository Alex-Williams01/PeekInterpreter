package main.java.ASTNodes.Unary;

import main.java.Token.Token;

public class DoubleNode extends UnaryNode {
    private double value;

    public DoubleNode(Token token) {
        this.value = Double.parseDouble(token.data());
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NumberNode{" +
                "value=" + value +
                '}';
    }
}
