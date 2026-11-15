package com.compiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, String> symbols = new HashMap<>();

    public boolean declare(String name, String type) {
        if (symbols.containsKey(name)) return false;
        symbols.put(name, type);
        return true;
    }

    public boolean isDeclared(String name) {
        return symbols.containsKey(name);
    }

    public String getType(String name) {
        return symbols.get(name);
    }


}