package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.way_nifty.strcat.scmj.Cell;
import com.way_nifty.strcat.scmj.Closure;
import com.way_nifty.strcat.scmj.Context;
import com.way_nifty.strcat.scmj.Expr;
import com.way_nifty.strcat.scmj.Num;
import com.way_nifty.strcat.scmj.Parser;
import com.way_nifty.strcat.scmj.Printer;
import com.way_nifty.strcat.scmj.ScmRuntimeException;
import com.way_nifty.strcat.scmj.Symbol;

public class ContextTest {
    
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
    
    private Expr parseAndEval(String src) {
        Parser parser = new Parser(new StringReader(src));
        try {
            Expr expr = parser.readExpr();
            return context.eval(expr);
        } catch (IOException e) {
            throw new InternalError();
        }
    }

    private Cell list(Expr ...exprs) {
        return Cell.list(exprs);
    }
    
    private Symbol sym(String name) {
        return Symbol.get(name);
    }

    private Num num(long n) {
        return new Num(n);
    }
    
    private void println(Expr expr) {
        System.out.println(Printer.toString(expr));
    }

    @Test
    public void testEvalNum() {
        Num num = new Num(123);
        Expr expr = context.eval(num);
        assertEquals(num, expr);
    }

    @Test
    public void testEval002() {
        Cell cell = list(Symbol.get("+"), new Num(3), new Num(5));
        println(cell);
        Expr expr = context.eval(cell);
        assertEquals(8, ((Num) expr).getValue());
    }

    @Test
    public void testEval003() {
        Context context = new Context();
        Cell cell =
                list(Symbol.get("+"), new Num(3),
                        list(Symbol.get("-"),
                                list(Symbol.get("*"), new Num(7),
                                        list(Symbol.get("/"), new Num(10), new Num(5))),
                                new Num(4)));
        System.out.println(Printer.toString(cell));
        Expr expr = context.eval(cell);
        assertEquals(13, ((Num) expr).getValue());
    }

    @Test
    public void testEvelLambda001() {
        Cell lambdaForm =
                list(sym("lambda"),
                        list(sym("x")),
                                list(sym("*"), num(2), sym("x")));
        println(lambdaForm);
        Expr closure = context.eval(lambdaForm);
        println(closure);
        assertTrue(closure instanceof Closure);
    }

    @Test
    public void testEvelLambda002() {
        Cell expr =
                list(list(sym("lambda"),
                        list(sym("x")),
                        list(sym("*"), num(2), sym("x"))),
                        num(7));
        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(14, ((Num) result).getValue());
    }

    @Test
    public void testLet001() {
        Cell expr =
                list(sym("let"),
                        list(list(sym("x"), num(2)), list(sym("y"), num(3))),
                        list(sym("+"), sym("x"), sym("y")));

        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(5, ((Num) result).getValue());
    }

    @Test
    public void testLet002() {
        //(let ((x 3)) 
        //  (let ((fun (lambda (y) (+ x y))))
        //        (+ (fun 1) (fun 2))))
        Cell expr =
                list(sym("let"), list(list(sym("x"), num(3))),
                        list(sym("let"), list(list(sym("fun"),
                                list(sym("lambda"), list(sym("y")),
                                        list(sym("+"), sym("x"), sym("y"))))),
                                        list(sym("+"), list(sym("fun"), num(1)), list(sym("fun"), num(2)))));
        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(9, ((Num) result).getValue());
    }

    @Test
    public void testLet003() {
        Expr result = parseAndEval("(let ((x (+ 1 2))) x)");
        assertEquals("3", toString(result));
    }

    @Test
    public void testIf001() {
        Cell expr =
                list(sym("let"),
                        list(list(sym("x"), num(2)), list(sym("y"), num(3))),
                        list(sym("if"), list(sym("<"), sym("x"), sym("y")),
                                sym("y")));
        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(3, ((Num) result).getValue());
    }

    @Test
    public void testIf002() {
        // 3.2
        // (let 
        //  ((fact (lambda (n) (if (< n 1) 1 (* n (fact (- n 1)))))))
        //  (fact 0))
        Cell expr =
                list(sym("let"),
                    list(list(sym("fact"),
                        list(sym("lambda"), list(sym("n")),
                        list(sym("if"), list(sym("<"), sym("n"), num(1)),
                                num(1),
                                list(sym("*"), sym("n"), list(sym("fact"), list(sym("-"), sym("n"), num(1)))))))),
                     list(sym("fact"), num(0)));
        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(1, ((Num) result).getValue());
    }

    @Test
    public void testIf003() {
        // 3.2
        // (letrec 
        //  ((fact (lambda (n) (if (< n 1) 1 (* n (fact (- n 1)))))))
        //  (fact 3))
        Cell expr =
                list(sym("letrec"),
                    list(list(sym("fact"),
                        list(sym("lambda"), list(sym("n")),
                        list(sym("if"), list(sym("<"), sym("n"), num(1)),
                                num(1),
                                list(sym("*"), sym("n"), list(sym("fact"), list(sym("-"), sym("n"), num(1)))))))),
                     list(sym("fact"), num(3)));
        println(expr);
        Expr result = context.eval(expr);
        println(result);
        assertTrue(result instanceof Num);
        assertEquals(6, ((Num) result).getValue());
    }
    
    @Test
    public void testQuote() {
        String src = "(quote (a b c))";
        Expr result = parseAndEval(src);
        assertEquals("(a b c)", toString(result));
    }

    @Test
    public void testDefineSymVal() {
        String src = "(define a 10)";
        Expr result = parseAndEval(src);
        assertEquals("a", toString(result));
        assertEquals("10", toString(context.eval(result)));
    }

    @Test
    public void testDefineFunctionParam1() {
        Expr result = parseAndEval("(define (func x) x)");
        assertEquals("func", toString(result));
        
        result = parseAndEval("(func 1)");
        assertEquals("1", toString(result));
    }

    @Test
    public void testDefineFunctionBody2() {
        Expr result = parseAndEval("(define (func x y) x y)");
        assertEquals("func", toString(result));
        
        result = parseAndEval("(func 10 20)");
        assertEquals("20", toString(result));
    }

    @Test
    public void testDefineFunctionNoParam() {
        Expr result = parseAndEval("(define (func) 5)");
        assertEquals("func", toString(result));
        
        result = parseAndEval("(func)");
        assertEquals("5", toString(result));
    }

    @Test
    public void testDefineFunctionRecursive() {
        Expr result = parseAndEval(
                "(define (fact n) (if (< n 1) 1 (* n (fact (- n 1)))))");
        assertEquals("fact", toString(result));
        
        result = parseAndEval("(fact 4)");
        assertEquals("24", toString(result));
    }

    @Test
    public void testSetq() {
        Expr result = parseAndEval("(let ((x 2)) (set! x 'A) x)");
        assertEquals("A", toString(result));
    }

    @Test
    public void testSetqUndefined() {
        try {
            parseAndEval("(set! a 1)");
            fail();
        } catch (ScmRuntimeException e) {
            assertTrue(e.getMessage().startsWith("Undefined variable: a"));
        }
    }

    @Test
    public void testCondNoElse() {
        Expr result = parseAndEval(
                "(define (test n) (cond ((= 10 n) 'a)) )");
        assertEquals("test", toString(result));
        
        result = parseAndEval("(test 10)");
        assertEquals("a", toString(result));

        result = parseAndEval("(test 11)");
        assertEquals("false", toString(result));
    }

    @Test
    public void testCondWithElse() {
        Expr result = parseAndEval(
                "(define (test n) (cond ((= 10 n) 'a) ((= 11 n) 'b) (else 'x)) )");
        assertEquals("test", toString(result));
        
        result = parseAndEval("(test 10)");
        assertEquals("a", toString(result));

        result = parseAndEval("(test 11)");
        assertEquals("b", toString(result));

        result = parseAndEval("(test 12)");
        assertEquals("x", toString(result));
    }
}
