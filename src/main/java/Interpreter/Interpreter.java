package main.java.Interpreter;

import main.java.ASTNodes.BinaryOperatorNode;
import main.java.ASTNodes.Node;
import main.java.ASTNodes.NumberNode;

public class Interpreter {
    public static double visit(Node node) {
        if (node instanceof NumberNode numberNode) {
            return numberNode.getValue();
        } else if (node instanceof BinaryOperatorNode binaryOperatorNode) {
            return switch(binaryOperatorNode.getOperatorType()) {
                case EQUAL -> visit(binaryOperatorNode.getRightNode());
                case ADD -> visit(binaryOperatorNode.getLeftNode()) + visit(binaryOperatorNode.getRightNode());
                case MINUS -> visit(binaryOperatorNode.getLeftNode()) - visit(binaryOperatorNode.getRightNode());
                case DIVIDE -> visit(binaryOperatorNode.getLeftNode()) / visit(binaryOperatorNode.getRightNode());
                case TIMES -> visit(binaryOperatorNode.getLeftNode()) * visit(binaryOperatorNode.getRightNode());
                default-> 0.0;
            };
        }
        return 0;
    }
}
