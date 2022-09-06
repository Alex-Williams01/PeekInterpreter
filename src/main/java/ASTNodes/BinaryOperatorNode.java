package main.java.ASTNodes;

import main.java.Token.Instruction;

public class BinaryOperatorNode extends Node {
    private Node leftNode;
    private Instruction operatorType;
    private Node rightNode;

    public BinaryOperatorNode(Node leftNode, Instruction operatorType, Node rightNode) {
        this.leftNode = leftNode;
        this.operatorType = operatorType;
        this.rightNode = rightNode;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Instruction getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Instruction operatorType) {
        this.operatorType = operatorType;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    @Override
    public String toString() {
        return "BinaryOperatorNode{" +
                "leftNode=" + leftNode +
                ", operatorType=" + operatorType +
                ", rightNode=" + rightNode +
                '}';
    }
}
