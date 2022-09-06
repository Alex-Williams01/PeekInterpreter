package main.java.Parser;

import main.java.ASTNodes.BinaryOperatorNode;
import main.java.ASTNodes.KeywordNode;
import main.java.ASTNodes.Node;
import main.java.ASTNodes.NumberNode;
import main.java.Exceptions.MissingTokenException;
import main.java.Exceptions.UnexpectedTokenException;
import main.java.Interpreter.Interpreter;
import main.java.Lexer.Lexer;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenList;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Parser {

    private final Lexer lexer;
    private List<Token> tokens;
    private int current = 0;

    private Token currentToken;

    private int lineNumber = 1;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void start() {
        TokenList line;
        while ((line = lexer.nextLine()) != null) {
            lineNumber = line.getLineNumber();
            var AST = parseLine(line);
            System.out.println(line);
            System.out.println(AST);
            System.out.println(Interpreter.visit(AST));
        }
    }

    private Node parseLine(TokenList line) {
        this.current = -1;
        this.tokens = line.getTokens();
        advance();
        var result = block();
        if (currentToken.instruction() != Instruction.EOL) {
            throw new UnexpectedTokenException("Unexpected token '%s',"
                    .formatted(currentToken.data()) + " Expected '+', '-', '/' or '*");
        }
        return result;
    }

    private Node block() {
        return statement();
    }

    private Node statement() {
        if (accept(Instruction.INT) || accept(Instruction.STRING))  {
            var identifierToken = currentToken;
            expect(Instruction.IDENTIFIER);
            expect(Instruction.EQUAL);
            return equality(identifierToken);
        } else {
            throw new UnexpectedTokenException("Syntax error, unexpected token '%s' on line %s%n"
                    .formatted(currentToken.data(), lineNumber));
        }
    }

    private BinaryOperatorNode equality(Token token) {
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

        if (accept(Instruction.NUMBER))  {
            return new NumberNode(tok);
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
        current += 1;
        if (current < tokens.size()) {
            currentToken = tokens.get(current);
        }
        return currentToken;
    }
}
