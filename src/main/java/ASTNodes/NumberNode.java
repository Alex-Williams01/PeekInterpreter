package main.java.ASTNodes;

import main.java.Token.Token;

public class NumberNode extends UnaryNode {
    private double value;

    public NumberNode(Token token) {
        this.value = Double.parseDouble(token.data());
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumberNode{" +
                "value=" + value +
                '}';
    }
}
