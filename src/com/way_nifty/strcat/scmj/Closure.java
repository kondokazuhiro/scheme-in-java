package com.way_nifty.strcat.scmj;

public class Closure implements Function {
    
    private final Cell parameters;
    private final Cell body;
    private final Env env;
    
    public Closure(Cell parameters, Cell body, Env env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }
    
    @Override
    public String toString() {
        return "<closure:" + hashCode() + ">";
    }
    
    public Cell getParameters() {
        return parameters;
    }

    public Cell getBody() {
        return body;
    }

    public Env getEnv() {
        return env;
    }
}
