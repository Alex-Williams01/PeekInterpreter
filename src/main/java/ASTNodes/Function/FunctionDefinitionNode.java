package main.java.ASTNodes.Function;

import main.java.ASTNodes.Node;
import main.java.Token.Instruction;
import main.java.Token.Token;

import java.util.List;

public class FunctionDefinitionNode extends Node {
    private final List<ParamNode> paramList;
    private final Instruction returnType;
    private final String functionName;
    private final Node body;

    public FunctionDefinitionNode(List<ParamNode> paramList, Instruction returnType, Token functionName, Node body) {
        this.paramList = paramList;
        this.returnType = returnType;
        this.functionName = functionName.data();
        this.body = body;
    }

    public List<ParamNode> getParamList() {
        return paramList;
    }

    public Instruction getReturnType() {
        return returnType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Node getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "FunctionDefinitionNode{" +
                "paramList=" + paramList +
                ", returnType=" + returnType +
                ", functionName='" + functionName + '\'' +
                ", body=" + body +
                '}';
    }
}
