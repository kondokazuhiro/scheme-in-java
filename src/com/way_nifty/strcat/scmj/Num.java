package com.way_nifty.strcat.scmj;

public class Num implements Expr {
    
    private final long value;

    public Num(long value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
    public long getValue() {
        return value;
    }
}
