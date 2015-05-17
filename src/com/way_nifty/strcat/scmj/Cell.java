package com.way_nifty.strcat.scmj;

import java.util.List;

public class Cell implements Expr {
    
    public static final Cell NIL = new Cell(null, null);

    private final Expr car;
    private Expr cdr;
    
    public Cell(Expr car, Expr cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public Cell(Expr car) {
        this(car, Cell.NIL);
    }

    public Expr getCar() {
        return car;
    }

    public Expr getCdr() {
        return cdr;
    }
    
    public void setCdr(Expr expr) {
        cdr = expr;
    }
    
    public Expr nth(int n) {
        return Cell.nth(n, this);
    }

    public Expr nthCdr(int n) {
        return Cell.nthCdr(n, this);
    }
    
    public static Expr nth(int n, Cell list) {
        Expr expr = nthCdr(n, list);
        return (expr != null && expr instanceof Cell) ? ((Cell) expr).getCar() : null;
    }
    
    public static Expr nthCdr(int n, Cell list) {
        Expr expr = list;
        for (int i = 0; i < n; i++) {
            if (expr instanceof Cell) {
                expr = ((Cell) expr).getCdr();
            }
        }
        return (expr instanceof Cell) ? expr : null;
    }
    
    public static Cell list(Expr ... exprs) {
        Cell cell = Cell.NIL;
        for (int i = exprs.length - 1; i >= 0; i--) {
            cell = new Cell(exprs[i], cell);
        }
        return cell;
    }

    public static Cell list(List<Expr> list) {
        return Cell.list(list.toArray(new Expr[list.size()]));
    }

}
