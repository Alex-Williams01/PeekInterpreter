package main.java.ASTNodes;

import main.java.Token.Token;

public class VariableReassignmentNode extends Node {

    private final String variableName;
    private final Node expression;

    public VariableReassignmentNode(Token variableName, Node expression) {
        this.variableName = variableName.data();
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "VariableAssignmentNode{" +
                "variableName='" + variableName + '\'' +
                ", expression=" + expression +
                '}';
    }
}
