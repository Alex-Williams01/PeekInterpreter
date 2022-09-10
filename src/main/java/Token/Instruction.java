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
    INCREMENT("++", "^\\+\\+$", OPERATOR_ADDITIVE),
    PRE_INCREMENT("Pre-increment", "^\\+\\+$",  OPERATOR_ADDITIVE),
    POST_INCREMENT("Post-increment", "^\\+\\+$", OPERATOR_ADDITIVE),
    DECREMENT("--", "^--$", OPERATOR_ADDITIVE),
    PRE_DECREMENT("Pre-decrement", "^--$",  OPERATOR_ADDITIVE),
    POST_DECREMENT("Post-decrement", "^--$", OPERATOR_ADDITIVE),
    POWER("power", "^\\^$", OPERATOR),
    LPAREN("left parenthesis", "^\\($", OPERATOR),
    RPAREN("right parenthesis", "^\\)$", OPERATOR),
    EQUAL("=", "^=$", OPERATOR_ASSIGNMENT),
    TERNARY_QUESTION("?", "^\\?$", CONDITIONAL),
    TERNARY_COLON(":", "^:$", CONDITIONAL),
    IF("if", "^if$", CONDITIONAL),
    ELSE("else", "^else$", CONDITIONAL),
    GREATER_THAN(">", "^>$", OPERATOR_RELATIONAL),
    GREATER_EQUAL(">=", "^>=$", OPERATOR_RELATIONAL),
    LESS_THAN("<", "^<$", OPERATOR_RELATIONAL),
    LESS_EQUAL("<=", "^<=$", OPERATOR_RELATIONAL),
    NOT_EQUAL("!=", "^!=$", OPERATOR_RELATIONAL),
    EQUALS("==", "^==$", OPERATOR_RELATIONAL),
    NOT("Not Operator (!)","^!$", OPERATOR_LOGICAL),
    AND("And", "^and$", OPERATOR_LOGICAL),
    OR("Or", "^or$", OPERATOR_LOGICAL),
    INT("int", "^int$", DATA_TYPE),
    STRING("String", "^String$", DATA_TYPE),
    BOOLEAN("Boolean", "^bool$", DATA_TYPE),
    PRINT("print", "^print$", OTHER),
    LBRACE("left brace", "^\\{$", OTHER),
    RBRACE("right brace", "^\\}$", OTHER),
    INT_LITERAL("int literal", "^\\d+$",  LITERAL),
    DOUBLE_LITERAL("double literal", "^\\d+[.]*\\d*$", LITERAL),
    STRING_LITERAL("String Literal", "^\".*\"$", LITERAL),
    BOOLEAN_LITERAL("Boolean Literal", "^true|false$", LITERAL),
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
                        Instruction::name,
                        Instruction::getPatternMatcher,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
    }

    public static Map<String, String> getOperators() {
        return getInstructionSet(instruction -> instruction.instructionType.name().contains("OPERATOR"));
    }

    public static Map<String, String> getLogicalOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_LOGICAL);
    }

    public static Map<String, String> getAdditiveOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_ADDITIVE);
    }

    public static Map<String, String> getMultiplicativeOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_MULTIPLICATIVE);
    }

    public static Map<String, String> getComparisonOperators() {
        return getInstructionSet(instruction -> instruction.instructionType == OPERATOR_RELATIONAL);
    }
}
