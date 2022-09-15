package main.java.ASTNodes.Function;

import main.java.ASTNodes.Node;
import main.java.Token.Instruction;
import main.java.Token.Token;

public class ParamNode extends Node {
    private Instruction dataType;
    private String name;

    public ParamNode(Instruction dataType, Token name) {
        this.dataType = dataType;
        this.name = name.data();
    }

    public Instruction getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ParamNode{" +
                "dataType=" + dataType +
                ", name='" + name + '\'' +
                '}';
    }
}
