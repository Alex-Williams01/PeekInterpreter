package main.java.ASTNodes.Loop;

import main.java.ASTNodes.Node;

public class LoopNode extends Node {
    private Node body;

    public LoopNode(Node body) {
        this.body = body;
    }

    public Node getBody() {
        return body;
    }
}
