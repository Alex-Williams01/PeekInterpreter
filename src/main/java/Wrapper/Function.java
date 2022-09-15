package main.java.Wrapper;

import main.java.ASTNodes.Function.FunctionDefinitionNode;
import main.java.ASTNodes.Node;
import main.java.Interpreter.Interpreter;
import main.java.SymbolTable.SymbolTable;
import main.java.Wrapper.Number.Double;
import main.java.Wrapper.Number.Integer;

import java.util.ArrayList;
import java.util.List;
import java.lang.String;

public class Function extends Object {
    private final String name;
    private final List<Param> params = new ArrayList<>();

    private final Node body;

    public Function(FunctionDefinitionNode functionDefinitionNode) {
        super(null);
        this.name = functionDefinitionNode.getFunctionName();
        functionDefinitionNode.getParamList().forEach( paramNode -> {
            var dataType = switch(paramNode.getDataType()) {
                case INT -> Integer.class;
                case DOUBLE -> Double.class;
                case STRING -> main.java.Wrapper.String.class;
                case BOOLEAN -> Boolean.class;
                default -> Object.class;
            };
            this.params.add(new Param(dataType, paramNode.getName()));
        });;
        this.body = functionDefinitionNode.getBody();
    }

    public Object execute(List<Object> args) {
        var index = 0;
        for(Object argument: args) {
            //TODO make this a contained symbol table
            SymbolTable.set(params.get(index).getName(), argument);
            index++;
        }
        return Interpreter.visit(body);
    }

    @Override
    public String parseString() {
        return "Function<%s>".formatted(name);
    }

    @Override
    public String toString() {
        return "Function{" +
                "name='" + name + '\'' +
                ", params=" + params +
                ", value=" + value +
                '}';
    }
}
