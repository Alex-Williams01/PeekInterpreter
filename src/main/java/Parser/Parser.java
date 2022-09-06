package main.java.Parser;

import main.java.ASTNodes.*;
import main.java.ASTNodes.Unary.*;
import main.java.Exceptions.MissingTokenException;
import main.java.Exceptions.UnexpectedTokenException;
import main.java.Lexer.Lexer;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenisedLine;

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
            System.out.println(line);
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
        return statement();
    }

    private Node statement() {
        if (accept(Instruction.STRING)) {
            return equality(currentToken);
        } else if (accept(Instruction.INT) || accept(Instruction.STRING))  {
            return equality(currentToken);
        } else {
            throw new UnexpectedTokenException("Syntax error, unexpected token '%s' on line %s%n"
                    .formatted(currentToken.data(), lineNumber));
        }
    }

    private BinaryOperatorNode equality(Token token) {
        expect(Instruction.IDENTIFIER);
        expect(Instruction.EQUAL);
        return new BinaryOperatorNode(new KeywordNode(token), Instruction.EQUAL, expression());
    }

    private Node expression() {
        return binaryOperator(this::term,  Instruction.getAdditiveOperators());
    }
    private Node term() {
        return binaryOperator(this::factor,  Instruction.getMultiplicativeOperators());
    }

    private Node binaryOperator(Supplier<Node> function, Map<String, String> operatorTypes) {
        Node left = function.get();
        while (operatorTypes.containsKey(currentToken.instruction().getPatternMatcher())) {
            var operatorToken = currentToken;
            advance();
            Node right = function.get();
            left = new BinaryOperatorNode(left, operatorToken.instruction(), right);
        }
        return left;
    }

    private Node factor() {
        var tok = currentToken;

        if (accept(Instruction.STRING_LITERAL)) {
            return new StringNode(tok);
        } else if (accept(Instruction.DOUBLE_LITERAL)) {
            return new DoubleNode(tok);
        } else if (accept(Instruction.INT_LITERAL))  {
            return new IntegerNode(tok);
        } else if (accept(Instruction.MINUS) || accept(Instruction.ADD)) {
            var value = currentToken;
            if (accept(Instruction.INT_LITERAL) || accept(Instruction.DOUBLE_LITERAL)) {
                return new UnaryOperatorNode(tok.instruction(), Double.parseDouble(value.data()));
            } else {
                throw new UnexpectedTokenException("Unexpected token '%s',"
                        .formatted(currentToken.data()) + " Expected Integer or Double");
            }
        } else if (accept(Instruction.LPAREN)) {
            var node = expression();
            expect(Instruction.RPAREN);
            return node;
        } else if (accept(Instruction.IDENTIFIER)) {
            return new KeywordNode(tok);
        } else {
            throw new RuntimeException("Syntax error: expected literal or variable");
        }
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

    private boolean hasMoreTokens() {
        return cursor < tokens.size();
    }

    private boolean isEOL() {
        return currentToken.instruction() == Instruction.EOL;
    }
}
