package com.way_nifty.strcat.scmj;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Interpreter {

    private Context context;
    
    public Interpreter() {
        this(new Context());
    }

    public Interpreter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Expr run(Reader reader) throws IOException {
        Parser parser = new Parser(reader);
        Expr result = Cell.NIL;
        Expr expr;
        while ((expr = parser.readExpr()) != null) {
            result = context.eval(expr);
        }
        return result;
    }
    
    public Expr repl() throws IOException {
        Parser parser = new Parser(new InputStreamReader(System.in));
        Expr result = Cell.NIL;
        
        while (true) {
            System.out.print(">>> ");
            Expr expr = parser.readExpr();
            if (expr == null) {
                break;
            }
            result = context.eval(expr);
            System.out.println(Printer.toString(result));
        }
        return result;
    }
    
    public static void main(String[] args) {
        String inputName = "";
        Reader reader = null;
        
        if (args.length == 0) {
            inputName = "(stdin)";
        } else if (args.length == 1) {
            inputName = args[0];
            try {
                reader = new FileReader(args[0]);
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.println(
                    "Usage: " + Interpreter.class.getSimpleName() + " [source]");
            System.exit(1);
        }
        
        Interpreter interpreter = new Interpreter();
        try {
            if (reader == null) {
                interpreter.repl();
            } else {
                interpreter.run(reader);
            }
        } catch (IOException e) {
            System.err.println("source: " + inputName);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
