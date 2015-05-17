package com.way_nifty.strcat.scmj;

public interface BuiltinFunction extends Function {
    public Expr apply(Expr args, Env env);
}
