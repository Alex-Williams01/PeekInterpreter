package main.java.ASTNodes.Unary;

import main.java.ASTNodes.Node;
import main.java.Token.Instruction;

public class UnaryOperatorNode extends UnaryNode {
    private final Instruction operator;
    private final Node value;

    public UnaryOperatorNode(Instruction operator, Node node) {
        this.operator = operator;
        this.value = node;
    }

    public Instruction getOperator() {
        return operator;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UnaryOperatorNode{" +
                "operator=" + operator +
                ", value=" + value +
                '}';
    }
}
