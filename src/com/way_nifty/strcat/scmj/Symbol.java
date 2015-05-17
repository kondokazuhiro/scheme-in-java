package com.way_nifty.strcat.scmj;

import java.util.HashMap;
import java.util.Map;

public class Symbol implements Expr {

    private static final Map<String, Symbol> symbolTable = new HashMap<>();
    
    public static final Symbol FALSE = Symbol.get("false");
    public static final Symbol TRUE = Symbol.get("true");
    public static final Symbol NIL = Symbol.get("nil");
    
    private final String name;
    
    public static Symbol get(String name) {
        Symbol symbol = symbolTable.get(name);
        if (symbol == null) {
            symbol = new Symbol(name);
            symbolTable.put(name, symbol);
        }
        return symbol;
    }
    
    private Symbol(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public String getName() {
        return name;
    }
}
