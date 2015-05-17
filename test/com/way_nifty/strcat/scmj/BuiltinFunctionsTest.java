package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.way_nifty.strcat.scmj.Context;
import com.way_nifty.strcat.scmj.Expr;
import com.way_nifty.strcat.scmj.Parser;
import com.way_nifty.strcat.scmj.Printer;

public class BuiltinFunctionsTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = new Context();
    }

    @After
    public void tearDown() throws Exception {
        context = null;
    }
    
    private String toString(Expr expr) {
        return Printer.toString(expr);
    }
    
    private Expr parseAndEval(String src) throws IOException {
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        return context.eval(expr);
    }

    @Test
    public void testCalcNum() throws IOException {
        assertEquals("3", toString(parseAndEval("(+ 1 2)")));
        assertEquals("-2", toString(parseAndEval("(- 1 3)")));
        assertEquals("6", toString(parseAndEval("(* 2 3)")));
        assertEquals("3", toString(parseAndEval("(/ 9 3)")));
        assertEquals("3", toString(parseAndEval("(/ 10 3)")));
    }
    
    @Test
    public void testRel() throws IOException {
        assertEquals("true", toString(parseAndEval("(< 1 2)")));
        assertEquals("false", toString(parseAndEval("(< 2 2)")));

        assertEquals("true", toString(parseAndEval("(<= 1 2)")));
        assertEquals("true", toString(parseAndEval("(<= 2 2)")));
        assertEquals("false", toString(parseAndEval("(<= 2 1)")));

        assertEquals("true", toString(parseAndEval("(> 2 1)")));
        assertEquals("false", toString(parseAndEval("(> 2 2)")));

        assertEquals("true", toString(parseAndEval("(>= 2 1)")));
        assertEquals("true", toString(parseAndEval("(>= 2 2)")));
        assertEquals("false", toString(parseAndEval("(>= 1 2)")));

        assertEquals("false", toString(parseAndEval("(= 2 1)")));
        assertEquals("true", toString(parseAndEval("(= 2 2)")));
        assertEquals("false", toString(parseAndEval("(= 1 2)")));
    }

    @Test
    public void testNullpFalse() throws IOException {
        String src = "(null? (list 1))";
        assertEquals("false", toString(parseAndEval(src)));
    }

    @Test
    public void testNullpTrue() throws IOException {
        String src = "(null? (list))";
        assertEquals("true", toString(parseAndEval(src)));
    }

    @Test
    public void testCar() throws IOException {
        String src = "(car (list 1 2 3))";
        assertEquals("1", toString(parseAndEval(src)));
    }

    @Test
    public void testCdr() throws IOException {
        String src = "(cdr (list 1 2 3))";
        assertEquals("(2 3)", toString(parseAndEval(src)));
    }

    @Test
    public void testCons() throws IOException {
        String src = "(cons 1 (list 2))";
        assertEquals("(1 2)", toString(parseAndEval(src)));
    }

    @Test
    public void testList() throws IOException {
        String src = "(list 1 2 3)";
        assertEquals("(1 2 3)", toString(parseAndEval(src)));
    }

    @Test
    public void testNot() throws IOException {
        assertEquals("true", toString(parseAndEval("(not false)")));
        assertEquals("false", toString(parseAndEval("(not true)")));
        assertEquals("false", toString(parseAndEval("(not nil)")));
        assertEquals("false", toString(parseAndEval("(not '())")));
        assertEquals("false", toString(parseAndEval("(not 0)")));
    }

    @Test
    public void testAtomp() throws IOException {
        assertEquals("true", toString(parseAndEval("(atom? 'a)")));
        assertEquals("true", toString(parseAndEval("(atom? 1)")));
        assertEquals("false", toString(parseAndEval("(atom? nil)")));
        assertEquals("false", toString(parseAndEval("(atom? '())")));
        assertEquals("false", toString(parseAndEval("(atom? '(a))")));
    }

    @Test
    public void testListp() throws IOException {
        assertEquals("true", toString(parseAndEval("(list? '())")));
        assertEquals("true", toString(parseAndEval("(list? nil)")));
        assertEquals("true", toString(parseAndEval("(list? '(a))")));
        assertEquals("false", toString(parseAndEval("(list? 1)")));
        assertEquals("false", toString(parseAndEval("(list? 'a)")));
    }

    @Test
    public void testNumberp() throws IOException {
        assertEquals("true", toString(parseAndEval("(number? 0)")));
        assertEquals("false", toString(parseAndEval("(number? 'a)")));
        assertEquals("false", toString(parseAndEval("(number? '(1))")));
    }
    
    @Test
    public void testEqp() throws IOException {
        assertEquals("true", toString(parseAndEval("(eq? 'a 'a)")));
        assertEquals("false", toString(parseAndEval("(eq? 1 1)")));
    }
}
