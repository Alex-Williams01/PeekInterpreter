package main.java.ASTNodes.Variables;

import main.java.ASTNodes.Node;
import main.java.Token.Instruction;
import main.java.Token.Token;

public class VariableAssignmentNode extends Node {

    private final String variableName;
    private final Class dataType;
    private final Node expression;

    public VariableAssignmentNode(Class dataType, Token variableName, Node expression) {
        this.dataType = dataType;
        this.variableName = variableName.data();
        this.expression = expression;
    }

    public Class getDataType() {
        return dataType;
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
