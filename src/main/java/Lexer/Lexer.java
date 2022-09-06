package main.java.Lexer;

import main.java.File.CodeReader;
import main.java.Token.Instruction;
import main.java.Token.Token;
import main.java.Token.TokenList;

import java.util.*;
import java.util.regex.Pattern;

import static main.java.File.CodeReader.END_OF_FILE;

public class Lexer {
    private static final Map<String, String> INSTRUCTION_SET = Instruction.getInstructionSet((ignored -> true));
    private static final List<Character> IGNORE = List.of(' ', '\t');

    private String line;

    private int lineNumber;
    private int charCount;
    private Character currentChar;
    private final CodeReader codeReader;

    public Lexer(String sourcePath) {
        codeReader = new CodeReader(sourcePath);
        lineNumber = 0;
    }

    public TokenList nextLine() {
        line = codeReader.nextLine();
        if (Objects.equals(line, END_OF_FILE)) {
            return null;
        }
        charCount = -1;
        advance();
        var tokenList = new TokenList(++lineNumber);
        tokeniseLine().forEach(tokenList::addToken);
        return tokenList;
    }

    private List<Token> tokeniseLine() {
        List<Token> tokens = new ArrayList<>();
        var currentToken = "";
        while (currentChar != null) {
            if (IGNORE.contains(currentChar)) {
                tokens.add(tokenise(currentToken));
                currentToken="";
            }
            else currentToken = currentToken.concat(currentChar.toString());
            advance();
            if (currentChar == null) {
                tokens.add(tokenise(currentToken));
            }
        }
        tokens.add(new Token("EOL", Instruction.EOL));
        return tokens;
    }

    private Token tokenise(String currentToken) {
        for (var entry : INSTRUCTION_SET.entrySet()) {
            var pattern = Pattern.compile(entry.getKey());
            if (pattern.matcher(currentToken).find()) {
                return  new Token(currentToken, Instruction.valueOf(entry.getValue()));
            }
        }
        return null;
    }

    private void advance() {
        charCount++;
        currentChar = charCount < line.length() ? line.charAt(charCount) : null;
    }
}
