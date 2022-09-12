package main.java.Lexer;

import main.java.File.CodeReader;
import main.java.File.Line;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenisedLine;

import java.util.*;
import java.util.regex.Pattern;

import static main.java.File.CodeReader.END_OF_FILE;

public class Lexer {
    private static final Map<String, String> INSTRUCTION_SET = Instruction.getInstructionSet((ignored -> true));
    private static final List<Character> IGNORE = List.of(' ', '\t');

    private Line line;
    private int charCount;
    private Character currentChar;
    private final CodeReader codeReader;

    public Lexer(String sourcePath) {
        codeReader = new CodeReader(sourcePath);
    }

    public TokenisedLine nextLine() {
        line = codeReader.nextLine();
        if (Objects.equals(line.data(), END_OF_FILE)) {
            return null;
        }
        charCount = -1;
        advance();
        return tokeniseLine(line.lineNumber());
    }

    private TokenisedLine tokeniseLine(int lineNumber) {
        var tokenList = new TokenisedLine(lineNumber);
        var currentToken = "";
        while (currentChar != null) {
            if (IGNORE.contains(currentChar)) {
                var token = tokenise(currentToken, INSTRUCTION_SET);
                if (token != null) {
                    tokenList.addToken(token);
                }
                currentToken="";
            }
            else if (currentChar.equals('"')) {
                tokenList.addToken(tokeniseString());
            }
            else {
                var token = tokenise(currentChar.toString(),  Instruction.getOperators());
                if (token != null) {
                    //check if comparison operator
                    if (isOperator(currentChar)) {
                        token = getOperatorOrDefault(token);
                    }

                    // if operator is directly after a token without a space
                    if (!currentToken.equals("")) {
                        tokenList.addToken(tokenise(currentToken, INSTRUCTION_SET));
                    }
                    tokenList.addToken(token);
                    currentToken = "";
                } else {
                    if (isOperator(currentChar)) {
                        token = getOperatorOrDefault(null);
                    }
                    if (token != null) {
                        if (!currentToken.equals("")) {
                            tokenList.addToken(tokenise(currentToken, INSTRUCTION_SET));
                        }
                        tokenList.addToken(token);
                        currentToken = "";
                    } else {
                        currentToken = currentToken.concat(currentChar.toString());
                    }
                }
            }
            advance();
        }
        var token = tokenise(currentToken,  INSTRUCTION_SET);
        if (token != null) {
            tokenList.addToken(token);
        }
        tokenList.addToken(new Token("EOL", Instruction.EOL));
        return tokenList;
    }

    private Token tokenise(String currentToken, Map<String, String> instructionSet) {
        for (var entry : instructionSet.entrySet()) {
            var pattern = Pattern.compile(entry.getValue());
            if (pattern.matcher(currentToken).find()) {
                return  new Token(currentToken, Instruction.valueOf(entry.getKey()));
            }
        }
        return null;
    }

    private Token getOperatorOrDefault(Token token) {
        var nextChar = peek();
        if (peek() == null) {
            return token;
        }
        var potentialComparison = currentChar.toString().concat(nextChar.toString());
        var comparisonToken = tokenise(potentialComparison, INSTRUCTION_SET);
        if (comparisonToken != null) {
            advance();
            return comparisonToken;
        }
        return token;
    }

    private Token tokeniseString() {
        var stringValue = "";
        advance();
        while (currentChar != '"') {
            stringValue = stringValue.concat(currentChar.toString());
            advance();
        }
        return new Token(stringValue, Instruction.STRING_LITERAL);
    }
    private void advance() {
        currentChar = ++charCount < line.data().length() ?
                line.data().charAt(charCount) : null;
    }

    private Character peek() {
        return charCount+1 < line.data().length() ?
                line.data().charAt(charCount+1) : null;
    }

    private boolean isOperator(Character character) {
        //TODO MAY BE ABLE TO CHANGE TO INSTRUCTION ENUM WHEN ADD ! UNARY OPERATOR
        return List.of('!','>','<','=', '+', '-', '*','/').contains(character);
    }
}
