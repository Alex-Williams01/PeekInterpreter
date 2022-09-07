package main.java.Interpreter;

import main.java.ASTNodes.BinaryOperatorNode;
import main.java.ASTNodes.Node;
import main.java.ASTNodes.Unary.*;
import main.java.ASTNodes.VariableAssignmentNode;
import main.java.Parser.Parser;
import main.java.SymbolTable.SymbolTable;
import main.java.Token.Instruction;
import main.java.Wrapper.Number.Double;
import main.java.Wrapper.Number.Integer;
import main.java.Wrapper.Number.Number;
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
            System.out.println(visit(AST.next()));
        }
    }
    public Object visit(Node node) {
        return switch(node) {
            case VariableAssignmentNode variableAssignmentNode -> visitVariableAssignmentNode(variableAssignmentNode);
            case VariableAccessNode variableAccessNode ->  SymbolTable.get(variableAccessNode.getValue());
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

    private Object visitVariableAssignmentNode(VariableAssignmentNode variableAssignmentNode) {
        var value = visit(variableAssignmentNode.getExpression());
        var varClass = variableAssignmentNode.getDataType();
        if (varClass.isInstance(value)) {
            SymbolTable.add(variableAssignmentNode.getVariableName(), value);
            return value;
        }
        //TODO REPLACE WITH CUSTOM EXCEPTION
        throw new ClassCastException("cannot convert %s to %s".formatted(
                value, varClass
        ));
    }

    private Object visitBinaryOperator(BinaryOperatorNode operatorNode) {
        var left = visit(operatorNode.getLeftNode());
        if (operatorNode.getOperatorType().equals(Instruction.EQUAL)) {
            return visit(operatorNode.getRightNode());
        }
        return switch(left) {
            case Integer leftInteger -> visitNumberNode(leftInteger, operatorNode, Integer.class);
            case Double leftDouble -> visitNumberNode(leftDouble, operatorNode, Double.class);
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

    private <T extends Number<T, U>, U> Number<T, U> visitNumberNode(T left,
                                                                     BinaryOperatorNode binaryOperatorNode,
                                                                     Class<T> clazz) {
        var rightUntyped =  visit(binaryOperatorNode.getRightNode());
        T right;
        try {
            //
            right = clazz.cast(rightUntyped);
        } catch (ClassCastException e) {
            //TODO replace with BadOperandException
            throw new ClassCastException("Cannot convert %s to %s".formatted(rightUntyped, left.getClass()));
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
