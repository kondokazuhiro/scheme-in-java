package com.way_nifty.strcat.scmj;

import java.util.Iterator;

public class CellIterator implements Iterator<Cell>, Iterable<Cell> {
    
    private Cell current;
    
    public CellIterator(Expr expr) {
        this.current = (expr instanceof Cell) ? (Cell) expr : Cell.NIL;
    }

    @Override
    public boolean hasNext() {
        return isContainer(current);
    }

    @Override
    public Cell next() {
        Cell result = current;
        current = isContainer(current.getCdr()) ? (Cell) current.getCdr() : Cell.NIL;
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Cell> iterator() {
        return this;
    }

    private boolean isContainer(Expr expr) {
        return expr instanceof Cell && expr != Cell.NIL;
    }
}
