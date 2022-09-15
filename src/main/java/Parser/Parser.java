package main.java.Parser;

import main.java.ASTNodes.*;
import main.java.ASTNodes.Function.FunctionCallNode;
import main.java.ASTNodes.Function.FunctionDefinitionNode;
import main.java.ASTNodes.Function.ParamNode;
import main.java.ASTNodes.Loop.ForNode;
import main.java.ASTNodes.Loop.WhileNode;
import main.java.ASTNodes.Unary.*;
import main.java.ASTNodes.Variables.VariableAssignmentNode;
import main.java.ASTNodes.Variables.VariableReassignmentNode;
import main.java.Exceptions.MissingTokenException;
import main.java.Exceptions.UnexpectedTokenException;
import main.java.Lexer.Lexer;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenisedLine;
import main.java.Wrapper.Boolean;
import main.java.Wrapper.Number.Double;
import main.java.Wrapper.Number.Integer;
import java.lang.String;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Parser {

    private final Lexer lexer;
    private List<Token> tokens;
    private int cursor = 0;

    private Token currentToken;

    private int lineNumber = 1;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Iterator<Node> parse() {
        TokenisedLine line;
        List<Node> abstractSyntaxTree = new ArrayList<>();
        while ((line = lexer.nextLine()) != null) {
            lineNumber = line.getLineNumber();
            abstractSyntaxTree.add(parseLine(line));
        }
        return abstractSyntaxTree.iterator();
    }

    private Node parseLine(TokenisedLine line) {
        this.cursor = -1;
        this.tokens = line.getTokens();
        advance();
        var result = block();
        if (!isEOL()) {
            throw new UnexpectedTokenException("Unexpected token '%s',"
                    .formatted(currentToken.data()) + " Expected '+', '-', '/' or '*");
        }
        return result;
    }

    private Node block() {
        if (accept(Instruction.LBRACE)) {
            var codeBlock = statement();
            expect(Instruction.RBRACE);
            return codeBlock;
        }
        return statement();
    }

    private Node statement() {
        if (accept(Instruction.IF)) {
            return branch();
        }
        return expression();
    }

    private Node branch() {
        expect(Instruction.LPAREN);
        var booleanExpression = expression();
        expect(Instruction.RPAREN);
        var happyPath = block();
        Node elsePath = new Node();
        if (accept(Instruction.ELSE)) {
            elsePath = block();
        }
        return new BranchingNode(booleanExpression, happyPath, elsePath);
    }

    private Node expression() {
        Node compExpression = compExpression();
        if (accept(Instruction.TERNARY_QUESTION)) {
            Node happyPath = block();
            expect(Instruction.TERNARY_COLON);
            Node elsePath = block();
            return new BranchingNode(compExpression, happyPath, elsePath);
        }
        return binaryOperator(() -> compExpression, Instruction.getLogicalOperators(), this::compExpression);
    }

    private Node compExpression() {
        if (accept(Instruction.NOT)) {
            return new UnaryOperatorNode(Instruction.NOT, compExpression());
        }
        return binaryOperator(this::arithExpression, Instruction.getComparisonOperators(), this::arithExpression);
    }

    private Node arithExpression() {
        return binaryOperator(this::term, Instruction.getAdditiveOperators(), this::term);
    }

    private Node term() {
        return binaryOperator(this::factor,  Instruction.getMultiplicativeOperators(), this::factor);
    }
    private Node factor() {
        var tok = currentToken;

        if (accept(Instruction.MINUS) || accept(Instruction.PLUS)) {
            var value = power();
            if (currentToken.instruction().equals(Instruction.EOL)) {
                return  new UnaryOperatorNode(tok.instruction(), value);
            }
            retreat();
            if (accept(Instruction.INT_LITERAL) || accept(Instruction.DOUBLE_LITERAL)
                    || accept(Instruction.IDENTIFIER)) {
                return new UnaryOperatorNode(tok.instruction(), value);
            } else {
                throw new UnexpectedTokenException("Unexpected token '%s',"
                        .formatted(currentToken.data()) + " Expected Integer or Double");
            }
        } else {
            return power();
        }
    }

    private Node power() {
        return binaryOperator(this::modulus, Map.of(Instruction.POWER.name(), Instruction.POWER.getPatternMatcher()), this::factor);
    }

    private Node modulus() {
        return binaryOperator(this::call, Map.of(Instruction.MODULUS.name(), Instruction.MODULUS.getPatternMatcher()), this::call);
    }

    private Node call() {
        var identifierName = currentToken;
        var atom = atom();
        if (accept(Instruction.LPAREN)) {
            //TODO UNIFY PARAM
            List<Node> params = new ArrayList<>();
            if (accept(Instruction.RPAREN)) {
                return new FunctionCallNode(params, identifierName);
            }
            params.add(expression());
            while (accept(Instruction.COMMA)) {
                params.add(expression());
            }
            expect(Instruction.RPAREN);
            return new FunctionCallNode(params, identifierName);
        }
        return atom;
    }

    private Node atom() {
        var token = currentToken;
        advance();
        return switch(token.instruction()) {
            case STRING_LITERAL -> new StringNode(token);
            case DOUBLE_LITERAL -> new DoubleNode(token);
            case INT_LITERAL -> new IntegerNode(token);
            case BOOLEAN_LITERAL -> new BooleanNode(token);
            case LPAREN -> {
                var node = expression();
                expect(Instruction.RPAREN);
                yield node;
            }
            case INCREMENT -> {
                var val = new VariableAccessNode(currentToken);
                expect(Instruction.IDENTIFIER);
                yield new UnaryOperatorNode(Instruction.PRE_INCREMENT, val);
            }
            case DECREMENT -> {
                var val = new VariableAccessNode(currentToken);
                expect(Instruction.IDENTIFIER);
                yield new UnaryOperatorNode(Instruction.PRE_DECREMENT, val);
            }
            case IDENTIFIER -> {
                Instruction instruction;
                Node value;
                if(accept(Instruction.INCREMENT)) {
                    yield new UnaryOperatorNode(Instruction.POST_INCREMENT, new VariableAccessNode(token));
                } else if (accept(Instruction.DECREMENT)) {
                    yield new UnaryOperatorNode(Instruction.POST_DECREMENT, new VariableAccessNode(token));
                } else if (accept(Instruction.EQUAL)) {
                    value = expression();
                } else if (accept(Instruction.PLUS_EQUAL)) {
                    instruction = Instruction.PLUS;
                    value = new BinaryOperatorNode(new VariableAccessNode(token), instruction, expression());
                } else if (accept(Instruction.MINUS_EQUAL)) {
                    instruction = Instruction.MINUS;
                    value = new BinaryOperatorNode(new VariableAccessNode(token), instruction, expression());
                } else if (accept(Instruction.DIVIDE_EQUAL)) {
                    instruction = Instruction.DIVIDE;
                    value = new BinaryOperatorNode(new VariableAccessNode(token), instruction, expression());
                } else if (accept(Instruction.TIMES_EQUAL)) {
                    instruction = Instruction.TIMES;
                    value = new BinaryOperatorNode(new VariableAccessNode(token), instruction, expression());
                } else {
                    yield new VariableAccessNode(token);
                }

                yield new VariableReassignmentNode(token, value);
            }
            case INT, STRING, BOOLEAN, DOUBLE ->  assignment(token, currentToken);
            case WHILE -> whileLoop();
            case FOR -> forLoop();
            case FUNCTION -> function();
            default -> throw new RuntimeException("Syntax error: expected literal or variable");
        };
    }

    private Node binaryOperator(Supplier<Node> function, Map<String, String> operatorTypes, Supplier<Node> secondaryFunction) {
        Node left = function.get();
        while (operatorTypes.containsValue(currentToken.instruction().getPatternMatcher())) {
            var operatorToken = currentToken;
            advance();
            Node right = secondaryFunction.get();
            left = new BinaryOperatorNode(left, operatorToken.instruction(), right);
        }
        return left;
    }

    private Node function() {
        var functionReturnType = getDataType();
        var functionName = currentToken;
        expect(Instruction.IDENTIFIER);
        expect(Instruction.LPAREN);
        List<ParamNode> params = new ArrayList<>();
        if(accept(Instruction.RPAREN)) {
            expect(Instruction.LBRACE);
            var functionBlock = block();
            expect(Instruction.RBRACE);
            return  new FunctionDefinitionNode(params, functionReturnType, functionName, functionBlock);
        }
        var dataType = getDataType();
        params.add(new ParamNode(dataType, currentToken));
        advance();
        while (accept(Instruction.COMMA)) {
            dataType = getDataType();
            params.add(new ParamNode(dataType, currentToken));
            advance();
        }
        expect(Instruction.RPAREN);
        expect(Instruction.LBRACE);
        var functionBlock = block();
        expect(Instruction.RBRACE);
        return new FunctionDefinitionNode(params, functionReturnType, functionName, functionBlock);
    }

    private Instruction getDataType() {
        var instructionType = currentToken.instruction();
        if(accept(Instruction.DOUBLE) || accept(Instruction.INT) ||
                accept(Instruction.BOOLEAN) || accept(Instruction.STRING)) {
            return instructionType;
        }
        throw new RuntimeException("AHHH");
    }

    private Node whileLoop() {
        expect(Instruction.LPAREN);
        var booleanExpression = expression();
        expect(Instruction.RPAREN);
        expect(Instruction.LBRACE);
        var body = block();
        expect(Instruction.RBRACE);
        return new WhileNode(booleanExpression, body);
    }

    private Node forLoop() {
        expect(Instruction.LPAREN);
        var start = expression();
        expect(Instruction.SEMICOLON);
        var booleanExpression = expression();
        expect(Instruction.SEMICOLON);
        var step = expression();
        expect(Instruction.RPAREN);
        expect(Instruction.LBRACE);
        var body = block();
        expect(Instruction.RBRACE);
        if (start instanceof VariableAssignmentNode) {
            return new ForNode(start, booleanExpression, step, body);
        }
        throw new RuntimeException("Invalid expression in for loop");
    }

    private Node assignment(Token dataTypeToken, Token token) {
        var dataType = switch(dataTypeToken.instruction()) {
            case STRING -> main.java.Wrapper.String.class;
            case INT -> Integer.class;
            case BOOLEAN -> Boolean.class;
            case DOUBLE -> Double.class;
            default -> Object.class;
        };
        expect(Instruction.IDENTIFIER);
        expect(Instruction.EQUAL);
        return new VariableAssignmentNode(dataType, token, expression());
    }
    private boolean expect(Instruction instruction) {
        if (accept(instruction)) {
            return true;
        }
        throw new MissingTokenException("Expected '%s', got %s on line %s%n"
                .formatted(instruction.getDisplayName(), currentToken.data(), lineNumber));
    }

    private boolean accept(Instruction instruction) {
        if (currentToken.instruction().equals(instruction)) {
            advance();
            return true;
        }
        return false;
    }

    private Token advance() {
        return hasMoreTokens() ?
                currentToken = tokens.get(++cursor) : null;
    }

    private Token retreat() {
        return cursor > 0 ?
                currentToken = tokens.get(--cursor) : null;
    }

    private boolean hasMoreTokens() {
        return cursor < tokens.size()-1;
    }

    private boolean isEOL() {
        return currentToken.instruction() == Instruction.EOL;
    }
}
