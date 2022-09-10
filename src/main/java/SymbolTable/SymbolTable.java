package main.java.SymbolTable;

import main.java.Wrapper.Object;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private static final Map<String,  Object> symbols = new HashMap<>();

    public static void set(String name, Object obj) {
        symbols.put(name, obj);
    }

    public static Object get(String name) {
        return symbols.get(name);
    }
}
