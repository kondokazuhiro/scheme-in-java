package com.way_nifty.strcat.scmj;

import java.util.HashMap;
import java.util.Map;

public class BuiltinFunctions {
    
    private static final Map<Symbol, BuiltinFunction> symFuncMap = new HashMap<>();

    static {
        symFuncMap.put(Symbol.get("+"), new Plus());
        symFuncMap.put(Symbol.get("-"), new Minus());
        symFuncMap.put(Symbol.get("*"), new Times());
        symFuncMap.put(Symbol.get("/"), new Divide());
        symFuncMap.put(Symbol.get("<"), new RelLT());
        symFuncMap.put(Symbol.get("<="), new RelLTE());
        symFuncMap.put(Symbol.get(">"), new RelGT());
        symFuncMap.put(Symbol.get(">="), new RelGTE());
        symFuncMap.put(Symbol.get("=="), new RelEQ());
        symFuncMap.put(Symbol.get("="), new RelEQ());

        symFuncMap.put(Symbol.get("null?"), new Nullp());
        symFuncMap.put(Symbol.get("not"), new Not());
        symFuncMap.put(Symbol.get("list?"), new Listp());
        symFuncMap.put(Symbol.get("atom?"), new Atomp());
        symFuncMap.put(Symbol.get("eq?"), new Eqp());
        symFuncMap.put(Symbol.get("number?"), new Numberp());

        symFuncMap.put(Symbol.get("car"), new Car());
        symFuncMap.put(Symbol.get("cdr"), new Cdr());
        symFuncMap.put(Symbol.get("cons"), new Cons());
        symFuncMap.put(Symbol.get("list"), new _List());
        symFuncMap.put(Symbol.get("newline"), new Newline());
        symFuncMap.put(Symbol.get("display"), new Display());
        symFuncMap.put(Symbol.get("print"), new Print());

        symFuncMap.put(Symbol.get("_apply_primitive_fun"), new ApplyBuiltinFunction());
    }
    
    public static void assignFunctionsToEnv(Env env) {
        for (Map.Entry<Symbol, BuiltinFunction> e : symFuncMap.entrySet()) {
            env.bind(e.getKey(), e.getValue());
        }
    }

    private static class Plus implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return new Num(n0.getValue() + n1.getValue());
        }
    }

    private static class Minus implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return new Num(n0.getValue() - n1.getValue());
        }
    }

    private static class Times implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return new Num(n0.getValue() * n1.getValue());
        }
    }

    private static class Divide implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return new Num(n0.getValue() / n1.getValue());
        }
    }

    private static class RelLT implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return toBooleanExpr(n0.getValue() < n1.getValue());
        }
    }
    
    private static class RelLTE implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return toBooleanExpr(n0.getValue() <= n1.getValue());
        }
    }
    
    private static class RelGT implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return toBooleanExpr(n0.getValue() > n1.getValue());
        }
    }
    
    private static class RelGTE implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return toBooleanExpr(n0.getValue() >= n1.getValue());
        }
    }
    
    private static class RelEQ implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Num n0 = (Num) Cell.nth(0, (Cell) args);
            Num n1 = (Num) Cell.nth(1, (Cell) args);
            return toBooleanExpr(n0.getValue() == n1.getValue());
        }
    }
    
    private static Expr toBooleanExpr(boolean value) {
        return value ? Symbol.TRUE : Symbol.FALSE;
    }
    
    private static class Nullp implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return toBooleanExpr(expr == Cell.NIL);
        }
    }

    private static class Not implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return toBooleanExpr(expr == Symbol.FALSE);
        }
    }

    private static class Listp implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return toBooleanExpr(expr instanceof Cell);
        }
    }

    private static class Atomp implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return toBooleanExpr(!(expr instanceof Cell));
        }
    }

    private static class Eqp implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr ex1 = Cell.nth(0, (Cell) args);
            Expr ex2 = Cell.nth(1, (Cell) args);
            return toBooleanExpr(ex1 == ex2);
        }
    }

    private static class Numberp implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return toBooleanExpr(expr instanceof Num);
        }
    }

    private static class Car implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return ((Cell) expr).getCar();
        }
    }

    private static class Cdr implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr expr = Cell.nth(0, (Cell) args);
            return ((Cell) expr).getCdr();
        }
    }

    private static class Cons implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Expr car = Cell.nth(0, (Cell) args);
            Expr cdr = Cell.nth(1, (Cell) args);
            return new Cell(car, cdr);
        }
    }

    private static class _List implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            return args;
        }
    }

    private static class Newline implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            System.out.println();
            return Cell.NIL;
        }
    }

    private static class Display implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            System.out.print(Printer.toString(Cell.nth(0, (Cell) args)));
            return Cell.NIL;
        }
    }

    private static class Print implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            for (Cell cell : new CellIterator(args)) {
                System.out.print(Printer.toString(cell.getCar()));
            }
            System.out.println();
            return Cell.NIL;
        }
    }

    private static class ApplyBuiltinFunction implements BuiltinFunction {
        @Override
        public Expr apply(Expr args, Env env) {
            Symbol sym = (Symbol) Cell.nth(0, (Cell) args);
            Cell funArgs = (Cell) Cell.nth(1, (Cell) args);
            BuiltinFunction fun = symFuncMap.get(sym);
            return fun.apply(funArgs, env);
        }
    }
}

