package main.java.ASTNodes.Unary;

import main.java.Token.Token;

public class VariableAccessNode extends UnaryNode {
    private String value;

    public VariableAccessNode(Token token) {
        this.value = token.data();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "VariableAccessNode{" +
                "value=" + value +
                '}';
    }
}
