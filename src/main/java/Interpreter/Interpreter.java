package main.java.Interpreter;

import main.java.ASTNodes.BinaryOperatorNode;
import main.java.ASTNodes.Node;
import main.java.ASTNodes.NumberNode;
import main.java.ASTNodes.UnaryOperatorNode;
import main.java.Parser.Parser;

public class Interpreter {

    private final Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public void start() {
        var AST = parser.parse();
        while (AST.hasNext()) {
            System.out.println(visit(AST.next()));
        }
    }
    public double visit(Node node) {
        if (node instanceof NumberNode numberNode) {
            return numberNode.getValue();
        } else if (node instanceof UnaryOperatorNode unaryOperatorNode) {
            return switch(unaryOperatorNode.getOperator()) {
                case ADD ->  unaryOperatorNode.getValue();
                case MINUS ->  unaryOperatorNode.getValue()*-1;
                default -> 0.0;
            };
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
