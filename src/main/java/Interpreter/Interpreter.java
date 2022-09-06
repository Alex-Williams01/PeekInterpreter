package main.java.Interpreter;

import main.java.ASTNodes.BinaryOperatorNode;
import main.java.ASTNodes.Node;
import main.java.ASTNodes.Unary.*;
import main.java.Parser.Parser;
import main.java.Token.Instruction;
import main.java.Wrapper.Double;
import main.java.Wrapper.Integer;
import main.java.Wrapper.Number;
import main.java.Wrapper.Object;
import main.java.Wrapper.String;

public class Interpreter {

    private final Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public void start() {
        var AST = parser.parse();
        while (AST.hasNext()) {
            System.out.println(visit(AST.next()).parseString());
        }
    }
    public Object visit(Node node) {
        return switch(node) {
            case KeywordNode keywordNode -> new Integer(1);
            case StringNode stringNode -> new String(stringNode.getValue());
            case DoubleNode doubleNode -> new Double(doubleNode.getValue());
            case IntegerNode integerNode -> new Integer(integerNode.getValue());
            case UnaryOperatorNode unaryOperatorNode -> visitUnaryOperator(unaryOperatorNode);
            case BinaryOperatorNode binaryOperatorNode -> visitBinaryOperator(binaryOperatorNode);
            default -> null;
        };
    }

    private Double visitUnaryOperator(UnaryOperatorNode node) {
        return switch(node.getOperator()) {
            case ADD ->  new Double(node.getValue());
            case MINUS ->  new Double(-node.getValue());
            default -> null;
        };
    }

    private Object visitBinaryOperator(BinaryOperatorNode operatorNode) {
        var left = visit(operatorNode.getLeftNode());
        if (operatorNode.getOperatorType().equals(Instruction.EQUAL)) {
            return visit(operatorNode.getRightNode());
        }
        return switch(left) {
            case Integer leftInteger -> visitNumberNode(leftInteger, operatorNode);
            case Double leftDouble -> visitNumberNode(leftDouble, operatorNode);
            case String leftString -> visitStringNode(leftString, operatorNode);
            default -> throw new IllegalStateException("Unexpected value: " + left);
        };
    }

    private String visitStringNode(String left, BinaryOperatorNode binaryOperatorNode) {
        var rightUntyped = visit(binaryOperatorNode.getRightNode());
        if (binaryOperatorNode.getOperatorType().equals(Instruction.ADD)) {
            return left.add(rightUntyped);
        } else {
            throw new ClassCastException("Cannot convert %s to a Double".formatted(rightUntyped));
        }
    }

    private <T extends Number<T>> Number<T> visitNumberNode(T left, BinaryOperatorNode binaryOperatorNode) {
        Object rightUntyped =  visit(binaryOperatorNode.getRightNode());
        T right;
        try {
            right =(T) rightUntyped;
        } catch (Exception e) {
            throw new ClassCastException("Cannot convert %s to an Integer".formatted(rightUntyped));
        }
        return switch(binaryOperatorNode.getOperatorType()) {
            case ADD -> left.add(right);
            case MINUS -> left.minus(right);
            case DIVIDE -> left.divideBy(right);
            case TIMES -> left.multiplyBy(right);
            default-> null;
        };
    }
}
