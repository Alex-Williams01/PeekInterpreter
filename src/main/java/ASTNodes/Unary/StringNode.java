package main.java.ASTNodes.Unary;

import main.java.Token.Token;

public class StringNode extends UnaryNode {

    private String value;

    public StringNode(Token token) {
        this.value = token.data();
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "StringNode{" +
                "value='" + value + '\'' +
                '}';
    }
}
