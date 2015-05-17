package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;

public class SchemeInRubyTest {

    private String rubyFormToScheme(InputStream in) throws IOException {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int ch;
        while ((ch = reader.read()) != -1) {
            switch (ch) {
            case '[':
                buf.append('(');
                break;
            case ']':
                buf.append(')');
                break;
            case ',':
                buf.append(' ');
                break;
            case '#':
                buf.append(';');
                break;
            case ':':
                break;
            default:
                buf.append((char) ch);
                break;
            }
        }
        return buf.toString();
    }
    
    private String toString(Expr expr) {
        return Printer.toString(expr);
    }

    @Test
    public void testFromRubyForm() throws Exception {
        InputStream in =
                getClass().getResourceAsStream("scheme_in_ruby_test.rb");
        String schemeSrc = rubyFormToScheme(in);
        in.close();
        
        Parser parser = new Parser(new StringReader(schemeSrc));
        Context context = new Context();
        Cell suite = (Cell) parser.readExpr();
        
        for (Cell cell : new CellIterator(suite)) {
            Cell pair = (Cell) cell.getCar();
            Expr expr = pair.nth(0);
            Expr expect = pair.nth(1);
            Expr actual = context.eval(expr);
            
            System.out.println("expr: " + toString(expr));
            System.out.println("expect: " + toString(expect));
            System.out.println("actual: " + toString(actual));
            
            assertEquals(toString(expect), toString(actual));
        }
    }

    @Test
    public void testREPL() throws Exception {
        Interpreter itp = new Interpreter();
        
        Expr result = itp.run(new StringReader(
                "(define (length list) (if (null? list) 0 (+ (length (cdr list)) 1)))"));
        assertEquals("length", toString(result));

        result = itp.run(new StringReader(
                "(length (list 1 2 3))"));
        assertEquals("3", toString(result));

        result = itp.run(new StringReader(
                "(letrec ((fact (lambda (n) (if (< n 1) 1 (* n (fact (- n 1))))))) (fact 3))"));
        assertEquals("6", toString(result));

        result = itp.run(new StringReader(
                "(let ((x 1)) (let ((dummy (set! x 2))) x))"));
        assertEquals("2", toString(result));
    }

    @Test
    public void testClosure() throws Exception {
        Interpreter itp = new Interpreter();
        
        String defineMakecounter =
                "(define (makecounter)" +
                "  (let ((count 0))" +
                "    (lambda ()" +
                "      (let ((dummy (set! count (+ count 1))))" +
                "   count))))";
        Expr result = itp.run(new StringReader(defineMakecounter));
        assertEquals("makecounter", toString(result));

        result = itp.run(new StringReader("(define inc (makecounter))"));
        assertEquals("inc", toString(result));

        result = itp.run(new StringReader("(inc)"));
        assertEquals("1", toString(result));

        result = itp.run(new StringReader("(inc)"));
        assertEquals("2", toString(result));
    }
}
