package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.way_nifty.strcat.scmj.Expr;
import com.way_nifty.strcat.scmj.Interpreter;
import com.way_nifty.strcat.scmj.Printer;

public class InterpreterTest {
    
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private String toString(Expr expr) {
        return Printer.toString(expr);
    }

    @Test
    public void testRun() throws Exception {
        String src =
                "(define (fact n) (if (< n 1) 1 (* n (fact (- n 1))))) \n" +
                "(define (plus a b) (+ a b)) \n" +
                "(fact (plus 3 1))\n";
        Reader reader = new StringReader(src);

        Interpreter itp = new Interpreter();
        Expr result = itp.run(reader);

        assertEquals("24", toString(result));
    }

    @Test
    public void testClosureCounter() throws Exception {
        String src =
                "(define (makecounter)" +
                "  (let ((count 0))" +
                "    (lambda ()" +
                "      (set! count (+ count 1))" +
                "      count)))" +
                "(define c1 (makecounter))" +
                "(define c2 (makecounter))" +
                "(c1)(c1)" +
                "(c2)" +
                "(+ (c1)(c2))";
        Reader reader = new StringReader(src);

        Interpreter itp = new Interpreter();
        Expr result = itp.run(reader);

        assertEquals("5", toString(result));
    }

    @Test
    public void testInternalDefine() throws Exception {
        Interpreter itp = new Interpreter();

        String src =
                "(define rev-iter 100)" +
                "(define (my-reverse x)" +
                "  (define (rev-iter list1 trail)" +
                "    (if (null? list1)" +
                "      trail" +
                "      (rev-iter (cdr list1) (cons (car list1) trail))))" +
                "  (rev-iter x '()))";

        Expr result = itp.run(new StringReader(src));
        assertEquals("my-reverse", toString(result));

        result = itp.run(new StringReader("(my-reverse '(0 1 2 3 4))"));
        assertEquals("(4 3 2 1 0)", toString(result));

        result = itp.run(new StringReader("rev-iter"));
        assertEquals("100", toString(result));

        result = itp.run(new StringReader("(set! rev-iter 200) rev-iter"));
        assertEquals("200", toString(result));

        result = itp.run(new StringReader("(my-reverse '(9 8 7 6))"));
        assertEquals("(6 7 8 9)", toString(result));
    }
}
