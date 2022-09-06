package main.java.ASTNodes;

import main.java.Token.Token;

public class KeywordNode extends UnaryNode {
    private String value;

    public KeywordNode(Token token) {
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
        return "KeywordNode{" +
                "value=" + value +
                '}';
    }
}
