package main.java.Parser;

import main.java.ASTNodes.*;
import main.java.ASTNodes.Unary.*;
import main.java.Exceptions.MissingTokenException;
import main.java.Exceptions.UnexpectedTokenException;
import main.java.Lexer.Lexer;
import main.java.SymbolTable.SymbolTable;
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
        var firstToken = currentToken;
        if (accept(Instruction.INT) || accept(Instruction.STRING) || accept(Instruction.BOOLEAN)
        || accept(Instruction.DOUBLE))  {
            return assignment(firstToken, currentToken);
        } else if (accept(Instruction.IF)) {
            return branch();
        }
        if (accept(Instruction.IDENTIFIER)) {
            Instruction instruction;
            Node value;
            if (accept(Instruction.EQUAL)) {
                value = expression();
            } else if (accept(Instruction.PLUS_EQUAL)) {
                instruction = Instruction.PLUS;
                value = new BinaryOperatorNode(new VariableAccessNode(firstToken), instruction, expression());
            } else if (accept(Instruction.MINUS_EQUAL)) {
                instruction = Instruction.MINUS;
                value = new BinaryOperatorNode(new VariableAccessNode(firstToken), instruction, expression());
            } else if (accept(Instruction.DIVIDE_EQUAL)) {
                instruction = Instruction.DIVIDE;
                value = new BinaryOperatorNode(new VariableAccessNode(firstToken), instruction, expression());
            } else if (accept(Instruction.TIMES_EQUAL)) {
                instruction = Instruction.TIMES;
                value = new BinaryOperatorNode(new VariableAccessNode(firstToken), instruction, expression());
            } else {
                retreat();
                return expression();
            }

            return new VariableReassignmentNode(firstToken, value);
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

    private Node reassignment(Token token) {
        var dataType = SymbolTable.get(token.data()).getClass();
        expect(Instruction.IDENTIFIER);
        expect(Instruction.EQUAL);
        return new VariableAssignmentNode(dataType, token, expression());
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

    private Node power() {
        return binaryOperator(this::modulus, Map.of(Instruction.POWER.name(), Instruction.POWER.getPatternMatcher()), this::factor);
    }

    private Node modulus() {
        return binaryOperator(this::atom, Map.of(Instruction.MODULUS.name(), Instruction.MODULUS.getPatternMatcher()), this::factor);
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
                if(accept(Instruction.INCREMENT)) {
                    yield new UnaryOperatorNode(Instruction.POST_INCREMENT, new VariableAccessNode(token));
                } else if (accept(Instruction.DECREMENT)) {
                    yield new UnaryOperatorNode(Instruction.POST_DECREMENT, new VariableAccessNode(token));
                }
                yield new VariableAccessNode(token);
            }
            default -> throw new RuntimeException("Syntax error: expected literal or variable");
        };
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
