package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemeInSchemeTest {
    
    private Interpreter interpreter = null;

    @Before
    public void setUp() throws Exception {
        InputStream in = getClass().getResourceAsStream("scheme_in_scheme.scm");
        interpreter = new Interpreter();
        interpreter.run(new InputStreamReader(in));
        in.close();
    }

    @After
    public void tearDown() throws Exception {
        interpreter = null;
    }
    
    private Expr run(String source) throws Exception {
        return interpreter.run(new StringReader(source));
    }

    private String runToS(String source) throws Exception {
        return toString(run(source));
    }
    
    private String toString(Expr expr) {
        return Printer.toString(expr);
    }

    @Test
    public void testBook() throws Exception {
        assertEquals("1",
                runToS("(__eval (quote 1) global_env)"));
        //assertEquals("(closure (x) (+ x 1))",
        //        runToS("(__eval '(lambda (x) (+ x 1)) global_env)"));
        assertEquals("true",
                runToS("(__eval '(num? 1) global_env)"));
        assertEquals("2",
                runToS("(__eval '(+ 1 1) global_env)"));
        assertEquals("2",
                runToS("(__eval '(- 3 1) global_env)"));
        assertEquals("3",
                runToS("(__eval '((lambda (x) (+ x 1)) 2) global_env)"));
        assertEquals("3",
                runToS("(__eval '((lambda (x y) (+ x y)) 1 2) global_env)"));
        assertEquals("1",
                runToS("(__eval '((lambda (x) (- ((lambda (x) x) 2) x)) 1) global_env)"));
    }
}
