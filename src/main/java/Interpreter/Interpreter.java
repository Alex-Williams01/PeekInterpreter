package main.java.Interpreter;

import main.java.ASTNodes.*;
import main.java.ASTNodes.Function.FunctionCallNode;
import main.java.ASTNodes.Function.FunctionDefinitionNode;
import main.java.ASTNodes.Loop.ForNode;
import main.java.ASTNodes.Loop.WhileNode;
import main.java.ASTNodes.Unary.*;
import main.java.ASTNodes.Variables.VariableAssignmentNode;
import main.java.ASTNodes.Variables.VariableReassignmentNode;
import main.java.Exceptions.UnexpectedTokenException;
import main.java.Parser.Parser;
import main.java.SymbolTable.SymbolTable;
import main.java.Token.Instruction;
import main.java.Wrapper.Boolean;
import main.java.Wrapper.Function;
import main.java.Wrapper.Number.Double;
import main.java.Wrapper.Number.Integer;
import main.java.Wrapper.Number.Number;
import main.java.Wrapper.Object;
import main.java.Wrapper.String;

import java.util.ArrayList;
import java.util.List;

public class Interpreter {

    private final Parser parser;

    public Interpreter(Parser parser) {
        this.parser = parser;
    }

    public void start() {
        var AST = parser.parse();
        while (AST.hasNext()) {
            var x = AST.next();
            System.out.println(x);
            System.out.println(visit(x));
        }
    }
    public static Object visit(Node node) {
        return switch(node) {
            case VariableAssignmentNode variableAssignmentNode -> visitVariableAssignmentNode(variableAssignmentNode);
            case VariableReassignmentNode variableReassignmentNode -> visitVariableReassignmentNode(variableReassignmentNode);
            case VariableAccessNode variableAccessNode ->  visitVariableAccessNode(variableAccessNode);
            case BranchingNode branchingNode -> visitBranchingNode(branchingNode);
            case StringNode stringNode -> visitUnaryNode(stringNode, 1);
            case DoubleNode doubleNode ->visitUnaryNode(doubleNode, 1);
            case IntegerNode integerNode -> visitUnaryNode(integerNode, 1);
            case BooleanNode booleanNode -> visitUnaryNode(booleanNode, 1);
            case UnaryOperatorNode unaryOperatorNode -> visitUnaryOperator(unaryOperatorNode);
            case BinaryOperatorNode binaryOperatorNode -> visitBinary(binaryOperatorNode);
            case WhileNode whileNode -> visitWhileNode(whileNode);
            case ForNode forNode -> visitForNode(forNode);
            case FunctionDefinitionNode functionDefinitionNode -> visitFunctionDefinitionNode(functionDefinitionNode);
            case FunctionCallNode functionCallNode -> visitFunctionCallNode(functionCallNode);
            default -> null;
        };
    }

    private static Object visitFunctionCallNode(FunctionCallNode functionCallNode) {
        var symbolValue = SymbolTable.get(functionCallNode.getFunctionName());
        if (symbolValue instanceof Function function) {
            List<Object> args = new ArrayList<>();
            functionCallNode.getArgumentList().forEach(node -> args.add(visit(node)));
            return function.execute(args);
        }
        throw new RuntimeException("AHHH");
    }

    private static Object visitFunctionDefinitionNode(FunctionDefinitionNode functionDefinitionNode) {
        var function = new Function(functionDefinitionNode);
        SymbolTable.set(functionDefinitionNode.getFunctionName(), function);
        return function;
    }

    private static Object visitWhileNode(WhileNode whileNode) {
        var condition = visit(whileNode.getBooleanExpressionNode());
        Object body = null;
        if (condition instanceof Boolean boolExpr) {
            while(boolExpr.getValue()) {
                body = visit(whileNode.getBody());
                boolExpr = (Boolean) visit(whileNode.getBooleanExpressionNode());
            }
        }
        return body;
    }

    private static Object visitForNode(ForNode forNode) {
        Object body = null;
        visit(forNode.getStartValue());
        Object condition = visit(forNode.getBooleanExpressionNode());
        if (condition instanceof Boolean boolExpr) {
            while(boolExpr.getValue()) {
                body = visit(forNode.getBody());
                visit(forNode.getStepNode());
                boolExpr = (Boolean) visit(forNode.getBooleanExpressionNode());
            }
        }
        return body;
    }

    private static Object visitVariableAccessNode(VariableAccessNode variableAccessNode) {
        var value = SymbolTable.get(variableAccessNode.getValue());
        if (value == null) throw new RuntimeException("variable '%s' does not exist".formatted(variableAccessNode.getValue()));
        return value;
    }

    private static Object visitBranchingNode(BranchingNode branchingNode) {
        Object booleanExpression = visit(branchingNode.getBooleanExpression());
        if (booleanExpression instanceof Boolean booleanExp) {
            if (booleanExp.getValue()) {
                return visit(branchingNode.getHappyPath());
            } else {
                return visit(branchingNode.getElsePath());
            }
        }
        throw new RuntimeException("BAD BOOLEAN EXPRESSION IN IF STATEMENT");
    }

    private static Object visitUnaryOperator(UnaryOperatorNode node) {
        var value = visit(node.getValue());
        return switch(node.getOperator()) {
            case PLUS ->  value;
            case MINUS ->  {
                if (value instanceof Number number) {
                    yield number.negated();
                }
                throw new RuntimeException("BAD OPERAND");
            }
            case NOT -> {
                if (value instanceof Boolean booleanExpression) {
                    yield booleanExpression.negate();
                }
                throw new RuntimeException("BAD OPERAND");
            }
            case PRE_INCREMENT -> {
                if (value instanceof Number number) {
                    var newValue = number.increment();
                    SymbolTable.set(((VariableAccessNode)node.getValue()).getValue(), newValue);
                    yield newValue;
                }
                throw new RuntimeException("BAD OPERAND");
            }
            case POST_INCREMENT -> {
                if (value instanceof Number number) {
                    SymbolTable.set(((VariableAccessNode)node.getValue()).getValue(), number.increment());
                    yield value;
                }
                throw new RuntimeException("BAD OPERAND");
            }
            case PRE_DECREMENT -> {
                if (value instanceof Number number) {
                    var newValue = number.decrement();
                    SymbolTable.set(((VariableAccessNode)node.getValue()).getValue(), newValue);
                    yield newValue;
                }
                throw new RuntimeException("BAD OPERAND");
            }
            case POST_DECREMENT -> {
                if (value instanceof Number number) {
                    SymbolTable.set(((VariableAccessNode)node.getValue()).getValue(), number.decrement());
                    yield value;
                }
                throw new RuntimeException("BAD OPERAND");
            }
            default -> null;
        };
    }

    private static Object visitUnaryNode(Node node, java.lang.Integer multiplier) {
        return switch(node) {
            case DoubleNode doubleNode -> new Double(multiplier*doubleNode.getValue());
            case IntegerNode integerNode -> new Integer(multiplier*integerNode.getValue());
            case BooleanNode booleanNode -> new Boolean(booleanNode.getValue());
            case StringNode stringNode -> new String(stringNode.getValue());
            default -> throw new IllegalStateException("Unexpected value: " + node);
        };
    }

    private static Object visitVariableAssignmentNode(VariableAssignmentNode variableAssignmentNode) {
        var value = visit(variableAssignmentNode.getExpression());
        var varClass = variableAssignmentNode.getDataType();
        if (varClass.isInstance(value)) {
            SymbolTable.set(variableAssignmentNode.getVariableName(), value);
            return value;
        } else if (value instanceof Integer integer && varClass.equals(Double.class)) {
            var promotedValue = new Double(integer.getValue());
            SymbolTable.set(variableAssignmentNode.getVariableName(), promotedValue);
            return promotedValue;
        }
        //TODO REPLACE WITH CUSTOM EXCEPTION
        throw new ClassCastException("cannot convert %s to %s".formatted(
                value, varClass
        ));
    }

    private static Object visitVariableReassignmentNode(VariableReassignmentNode variableReassignmentNode) {
        if (!SymbolTable.exists(variableReassignmentNode.getVariableName())) {
            throw new RuntimeException("Identifier '%s' does not exist".formatted(variableReassignmentNode.getVariableName()));
        }
        var value = visit(variableReassignmentNode.getExpression());
        var varClass = SymbolTable.get(variableReassignmentNode.getVariableName()).getClass();
        if (varClass.isInstance(value)) {
            SymbolTable.set(variableReassignmentNode.getVariableName(), value);
            return value;
        }
        //TODO REPLACE WITH CUSTOM EXCEPTION
        throw new ClassCastException("cannot convert %s to %s".formatted(
                value, varClass
        ));
    }

    private static Object visitBinary(BinaryOperatorNode operatorNode) {
        var operator = operatorNode.getOperatorType().getInstructionType();
        return switch (operator) {
            case OPERATOR_ADDITIVE,
                    OPERATOR_MULTIPLICATIVE, OPERATOR -> visitBinaryOperator(operatorNode);
            case OPERATOR_RELATIONAL -> visitComparisonOperator(operatorNode);
            case OPERATOR_LOGICAL -> visitLogicalOperator(operatorNode);
            default -> throw new RuntimeException("AHHH");
        };
    }

    private static Object visitLogicalOperator(BinaryOperatorNode operatorNode) {
        var operator = operatorNode.getOperatorType();
        try {
            Boolean left = (Boolean) visit(operatorNode.getLeftNode());
            Boolean right = (Boolean) visit(operatorNode.getRightNode());
            return switch(operator) {
                case AND -> left.and(right);
                case OR -> left.or(right);
                case XOR -> left.exclusiveOr(right);
                default -> throw new IllegalStateException("Unexpected value: " + operator);
            };
        } catch (ClassCastException e) {
            throw new RuntimeException("BAD CAST OPERAND BOOLEAN");
        }
    }

    private static Object visitComparisonOperator(BinaryOperatorNode operatorNode) {
        var operator = operatorNode.getOperatorType();
        var left = visit(operatorNode.getLeftNode());
        var right = visit(operatorNode.getRightNode());
        try {
            return switch (operator) {
                case NOT_EQUAL -> new Boolean(!left.getValue().equals(right.getValue()));
                case EQUALS -> new Boolean(left.getValue().equals(right.getValue()));
                case LESS_EQUAL -> ((Number)left).lessThanEqual((Number)right);
                case GREATER_EQUAL -> ((Number)left).greaterThanEqual((Number)right);
                case LESS_THAN -> ((Number)left).lessThan((Number)right);
                case GREATER_THAN -> ((Number)left).greaterThan((Number)right);
                default -> throw new RuntimeException("AHHH");
            };
        } catch (ClassCastException e) {
            //TODO REPLACE WITH BAD OPERAND EXCEPTION
            throw new RuntimeException("Bad Operand for comparison");
        }

    }

    private static Object visitBinaryOperator(BinaryOperatorNode operatorNode) {
        var left = visit(operatorNode.getLeftNode());
        var right = visit(operatorNode.getRightNode());

        if(left instanceof Number leftNum && right instanceof Number rightNum) {
            leftNum = promoteNumber(leftNum, rightNum.getClass());
            rightNum = promoteNumber(rightNum, leftNum.getClass());
            return switch(leftNum) {
                case Integer leftInteger -> visitNumberNode(leftInteger, (Integer)rightNum, operatorNode, Integer.class);
                case Double leftDouble -> visitNumberNode(leftDouble, (Double) rightNum, operatorNode, Double.class);
                default -> throw new IllegalStateException("Unexpected value: " + left);
            };
        }

        return switch(left) {
            case String leftString -> visitStringNode(leftString, operatorNode);
            default -> throw new IllegalStateException("Unexpected value: " + left);
        };
    }

    private static <T extends Number<T, U>, U> Number promoteNumber(Number value, Class<T> clazz) {
        if (value instanceof Integer integer && clazz.equals(Double.class)) {
            return new Double(integer.getValue());
        }
        return value;
    }

    private static Object visitStringNode(String left, BinaryOperatorNode binaryOperatorNode) {
        var rightUntyped = visit(binaryOperatorNode.getRightNode());
        if (binaryOperatorNode.getOperatorType().equals(Instruction.PLUS)) {
            return left.add(rightUntyped);
        } else {
            throw new UnexpectedTokenException("Unknown operator %s".formatted(binaryOperatorNode));
        }
    }

    private static <T extends Number<T, U>, U> Number<T, U> visitNumberNode(T left, T right,
                                                                            BinaryOperatorNode binaryOperatorNode,
                                                                            Class<T> clazz) {
        return switch(binaryOperatorNode.getOperatorType()) {
            case POWER -> left.pow(right);
            case PLUS -> left.add(right);
            case MINUS -> left.minus(right);
            case DIVIDE -> left.divideBy(right);
            case TIMES -> left.multiplyBy(right);
            case MODULUS -> left.modulus(right);
            default-> null;
        };
    }
}
