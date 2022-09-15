package main.java.Wrapper;
import main.java.ASTNodes.Node;

import java.lang.String;

public class Param {
    private final Class<? extends Object> dataType;
    private final String name;

    public Param(Class<? extends Object> dataType, String name) {
        this.dataType = dataType;
        this.name = name;
    }

    public Class<? extends Object> getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Param{" +
                "dataType=" + dataType +
                ", name='" + name + '\'' +
                '}';
    }
}
