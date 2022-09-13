package main.java.ASTNodes.Loop;

import main.java.ASTNodes.Node;

public class ForNode extends LoopNode {
    private final Node startValue;
    private final Node booleanExpressionNode;
    private final Node stepNode;

    public ForNode(Node startValue, Node booleanExpressionNode,
                   Node stepNode, Node body) {
        super(body);
        this.startValue = startValue;
        this.booleanExpressionNode = booleanExpressionNode;
        this.stepNode = stepNode;
    }

    public Node getStartValue() {
        return startValue;
    }

    public Node getBooleanExpressionNode() {
        return booleanExpressionNode;
    }

    public Node getStepNode() {
        return stepNode;
    }
}
