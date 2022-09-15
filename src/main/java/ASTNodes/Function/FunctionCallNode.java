package main.java.ASTNodes.Function;

import main.java.ASTNodes.Node;
import main.java.Token.Token;

import java.util.List;

public class FunctionCallNode extends Node {
    private final List<Node> argumentList;
    private final String functionName;

    public FunctionCallNode(List<Node> argumentList, Token functionName) {
        this.argumentList = argumentList;
        this.functionName = functionName.data();
    }

    public List<Node> getArgumentList() {
        return argumentList;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public String toString() {
        return "FunctionCallNode{" +
                "argumentList=" + argumentList +
                ", functionName='" + functionName + '\'' +
                '}';
    }
}
