package main.java.ASTNodes.Unary;

import main.java.Token.Instruction;

public class UnaryOperatorNode extends UnaryNode {
    private final Instruction operator;
    private final double value;

    public UnaryOperatorNode(Instruction operator, double value) {
        this.operator = operator;
        this.value = value;
    }

    public Instruction getOperator() {
        return operator;
    }

    public double getValue() {
        return value;
    }
}
