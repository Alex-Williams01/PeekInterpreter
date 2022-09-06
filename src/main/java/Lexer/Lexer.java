package main.java.Lexer;

import main.java.File.CodeReader;
import main.java.File.Line;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenList;

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

    public TokenList nextLine() {
        line = codeReader.nextLine();
        if (Objects.equals(line.data(), END_OF_FILE)) {
            return null;
        }
        charCount = -1;
        advance();
        return tokeniseLine(line.lineNumber());
    }

    private TokenList tokeniseLine(int lineNumber) {
        var tokenList = new TokenList(lineNumber);
        var currentToken = "";
        while (currentChar != null) {
            if (IGNORE.contains(currentChar)) {
                var token = tokenise(currentToken, INSTRUCTION_SET);
                if (token != null) {
                    tokenList.addToken(token);
                }
                currentToken="";
            }
            else {
                var token = tokenise(currentChar.toString(),  Instruction.getOperators());
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
            var pattern = Pattern.compile(entry.getKey());
            if (pattern.matcher(currentToken).find()) {
                return  new Token(currentToken, Instruction.valueOf(entry.getValue()));
            }
        }
        return null;
    }

    private void advance() {
        currentChar = ++charCount < line.data().length() ?
                line.data().charAt(charCount) : null;
    }
}
