package com.way_nifty.strcat.scmj;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class Printer {

    public static void write(Expr expr, Writer writer) throws IOException {
        if (!isList(expr)) {
            writer.append(expr.toString());
        } else {
            writer.append("(");
            while (isList(expr) && expr != Cell.NIL) {
                Cell cell = (Cell) expr;
                write(cell.getCar(), writer);
                expr = cell.getCdr();
                if (isList(expr) && expr != Cell.NIL) {
                    writer.append(" ");
                }
            }
            writer.append(")");
        }
    }
    
    public static String toString(Expr expr) {
        StringWriter writer = new StringWriter();
        try {
            write(expr, writer);
            return writer.getBuffer().toString();
        } catch (IOException e) {
            throw new InternalError();
        }
    }
    
    private static boolean isList(Expr expr) {
        return expr instanceof Cell;
    }
}
