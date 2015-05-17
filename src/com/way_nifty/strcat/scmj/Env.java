package com.way_nifty.strcat.scmj;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Env {

    private final LinkedList<Map<Symbol, Expr>> envList = new LinkedList<>();

    public Env() {
        envList.addFirst(new HashMap<Symbol, Expr>());
    }
    
    private Env(Env env) {
        this.envList.addAll(env.envList);
    }

    public Env extendEnv() {
        Env env = new Env(this);
        env.envList.addFirst(new HashMap<Symbol, Expr>());
        return env;
    }
    
    public void bind(Symbol symbol, Expr expr) {
        Map<Symbol, Expr> map = envList.getFirst();
        map.put(symbol, expr);
    }

    public void set(Symbol symbol, Expr expr) {
        for (Map<Symbol, Expr> env : envList) {
            if (env.containsKey(symbol)) {
                env.put(symbol, expr);
                return;
            }
        }
        throw new ScmRuntimeException("Undefined variable: " + symbol.getName());
    }
    
    public Expr getValue(Symbol symbol) {
        for (Map<Symbol, Expr> env : envList) {
            Expr expr = env.get(symbol);
            if (expr != null) {
                return expr;
            }
        }
        return null;
    }
}
