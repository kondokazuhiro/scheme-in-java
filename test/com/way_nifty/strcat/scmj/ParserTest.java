package com.way_nifty.strcat.scmj;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.way_nifty.strcat.scmj.Context;
import com.way_nifty.strcat.scmj.Expr;
import com.way_nifty.strcat.scmj.Num;
import com.way_nifty.strcat.scmj.Parser;
import com.way_nifty.strcat.scmj.Printer;
import com.way_nifty.strcat.scmj.Symbol;

public class ParserTest {
    
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

    @Test
    public void testReadSymbol() throws IOException {
        String src = "a";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertTrue(expr instanceof Symbol);
        assertEquals("a", toString(expr));
        
        assertNull(parser.readExpr());
    }

    @Test
    public void testReadSymbolWithSpace() throws IOException {
        String src = " _sym ";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertTrue(expr instanceof Symbol);
        assertEquals("_sym", toString(expr));

        assertNull(parser.readExpr());
    }

    @Test
    public void testReadNum() throws IOException {
        String src = " 123456 ";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertTrue(expr instanceof Num);
        assertEquals("123456", toString(expr));
    }

    @Test
    public void testReadList000() throws IOException {
        String src = "()";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("()", toString(expr));
    }

    @Test
    public void testReadList001() throws IOException {
        String src = "(sym)";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(sym)", toString(expr));
    }

    @Test
    public void testReadList002() throws IOException {
        String src = "(sym 123)";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(sym 123)", toString(expr));
    }

    @Test
    public void testReadList003() throws IOException {
        String src = "(a\n b\r\n c)";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(a b c)", toString(expr));
    }

    @Test
    public void testReadList004() throws IOException {
        String src = "  ((a)( ( b)678 )  )";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("((a) ((b) 678))", toString(expr));
    }

    @Test
    public void testReadList005() throws IOException {
        String src =
                "(letrec ((fact (lambda (n) (if (< n 1) 1 (* n (fact (- n 1))))))) (fact 3))";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals(src, toString(expr));
        
        Expr result = context.eval(expr);
        assertEquals("6", toString(result));
    }

    @Test
    public void testQuoteSymbol() throws IOException {
        String src = "'sym";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(quote sym)", toString(expr));
    }

    @Test
    public void testQuoteList1() throws IOException {
        String src = "'(a)";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(quote (a))", toString(expr));
    }

    @Test
    public void testQuoteList2() throws IOException {
        String src = "''(a (b))";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(quote (quote (a (b))))", toString(expr));
    }

    @Test
    public void testComment001() throws IOException {
        String src = "a;comment";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("a", toString(expr));
    }

    @Test
    public void testComment002() throws IOException {
        String src = "(;\nA;c\nB;c\n3;c\n';c\nD);";
        Parser parser = new Parser(new StringReader(src));
        Expr expr = parser.readExpr();
        
        assertEquals("(A B 3 (quote D))", toString(expr));
    }
}
