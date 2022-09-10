package main.java.ASTNodes;

public class BranchingNode extends Node {
    private final Node booleanExpression;
    private final Node happyPath;
    private final Node elsePath;

    public BranchingNode(Node booleanExpression, Node happyPath, Node elsePath) {
        this.booleanExpression = booleanExpression;
        this.happyPath = happyPath;
        this.elsePath = elsePath;
    }

    public Node getBooleanExpression() {
        return booleanExpression;
    }

    public Node getHappyPath() {
        return happyPath;
    }

    public Node getElsePath() {
        return elsePath;
    }

    @Override
    public String toString() {
        return "BranchingNode{" +
                "booleanExpression=" + booleanExpression +
                ", happyPath=" + happyPath +
                ", elsePath=" + elsePath +
                '}';
    }
}
