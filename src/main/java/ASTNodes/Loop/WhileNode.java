package main.java.ASTNodes.Loop;

import main.java.ASTNodes.Node;
import main.java.Token.Token;

public class WhileNode extends LoopNode {
    private final Node booleanExpressionNode;

    public WhileNode(Node booleanExpressionNode, Node body) {
        super(body);
        this.booleanExpressionNode = booleanExpressionNode;
    }

    public Node getBooleanExpressionNode() {
        return booleanExpressionNode;
    }
}
