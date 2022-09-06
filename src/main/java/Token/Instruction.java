package main.java.Token;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static main.java.Token.InstructionType.*;

public enum Instruction {
    TIMES("times", "^\\*$", OPERATOR_MULTIPLICATIVE),
    DIVIDE("divide", "^/$", OPERATOR_MULTIPLICATIVE),
    ADD("add", "^\\+$", OPERATOR_ADDITIVE),
    MINUS("minus", "^-$", OPERATOR_ADDITIVE),
    LPAREN("left parenthesis", "^\\($", OPERATOR),
    RPAREN("right parenthesis", "^\\)$", OPERATOR),
    EQUAL("=", "^=$", OPERATOR),
    IF("if", "^if$", CONDITIONAL),
    ELSE("else", "^else$", CONDITIONAL),
    GREATER_EQUAL("greater equals", "^>=$", EQUALITY),
    INT("int", "^int$", DATA_TYPE),
    STRING("string", "^string$", DATA_TYPE),
    PRINT("print", "^print$", OTHER),
    LBRACE("left brace", "^\\{$", OTHER),
    RBRACE("right brace", "^\\}$", OTHER),
    NUMBER("number", "^\\d+[.]*\\d*$", OTHER),
    IDENTIFIER("identifier", "^[A-Za-z_][a-zA-Z_0-9]*$", OTHER),
    EOL("End of Line", "EOF", OTHER);

    private final String displayName;
    private final String patternMatcher;
    private final InstructionType instructionType;

    Instruction(String displayName, String patternMatcher, InstructionType instructionType) {
        this.displayName = displayName;
        this.patternMatcher = patternMatcher;
        this.instructionType = instructionType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPatternMatcher() {
        return patternMatcher;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public static Map<String, String> getInstructionSet(Predicate<Instruction> predicate) {
        return Arrays.stream(Instruction.values())
                .filter(predicate)
                .collect(Collectors.toMap(
                        Instruction::getPatternMatcher,
                        Instruction::name,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    public static Map<String, String> getAdditiveOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_ADDITIVE);
    }

    public static Map<String, String> getMultiplicativeOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_MULTIPLICATIVE);
    }
}
