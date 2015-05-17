package com.way_nifty.strcat.scmj;

import java.util.LinkedList;

public class Context {
    
    private static final Symbol SYM_QUOTE = Symbol.get("quote");
    private static final Symbol SYM_LAMBDA = Symbol.get("lambda");
    private static final Symbol SYM_LET = Symbol.get("let");
    private static final Symbol SYM_LETREC = Symbol.get("letrec");
    private static final Symbol SYM_IF = Symbol.get("if");
    private static final Symbol SYM_COND = Symbol.get("cond");
    private static final Symbol SYM_ELSE = Symbol.get("else");
    private static final Symbol SYM_DEFINE = Symbol.get("define");
    private static final Symbol SYM_SETQ = Symbol.get("set!");

    private final Env globalEnv;
    
    public Context() {
        this(createDefaultEnv());
    }
    
    public Context(Env globalEnv) {
        this.globalEnv = globalEnv;
    }
    
    public static Env createDefaultEnv() {
        Env env = new Env();
        
        env.bind(Symbol.NIL, Cell.NIL);
        env.bind(Symbol.TRUE, Symbol.TRUE);
        env.bind(Symbol.FALSE, Symbol.FALSE);
        
        BuiltinFunctions.assignFunctionsToEnv(env);
        
        return env;
    }
    
    public Expr eval(Expr expr) {
        return eval(expr, globalEnv);
    }
    
    protected Expr eval(Expr expr, Env env) {
        if (!isList(expr)) {
            if (isImmidiate(expr) || isClosure(expr)) {
                return expr;
            }
            if (isSymbol(expr)) {
                return symbolValue((Symbol) expr, env);
            }
        } else if (isSpecialForm(expr)) {
            return evalSpecialForm(expr, env);
        } else {
            Cell cell = (Cell) expr;
            Function func = (Function) eval(cell.getCar(), env);
            Expr args = evalList(cell.getCdr(), env);
            return apply(func, args);
        }
        
        throw new ScmRuntimeException(
                "Unknown expression: " + Printer.toString(expr));
    }
    
    private Expr evalSpecialForm(Expr expr, Env env) {
        if (carMatches(expr, SYM_QUOTE)) {
            return evalQuote((Cell) expr, env);
        }
        if (carMatches(expr, SYM_LAMBDA)) {
            return evalLambda((Cell) expr, env);
        }
        if (carMatches(expr, SYM_LET)) {
            return evalLet((Cell) expr, env);
        }
        if (carMatches(expr, SYM_LETREC)) {
            return evalLetrec((Cell) expr, env);
        }
        if (carMatches(expr, SYM_IF)) {
            return evalIf((Cell) expr, env);
        }
        if (carMatches(expr, SYM_COND)) {
            return evalCond((Cell) expr, env);
        }
        if (carMatches(expr, SYM_DEFINE)) {
            return evalDefine((Cell) expr, env);
        }
        if (carMatches(expr, SYM_SETQ)) {
            return evalSetq((Cell) expr, env);
        }
        throw new InternalError("Unknown expression.");
    }
    
    private Expr evalQuote(Cell cell, Env env) {
        return cell.nth(1);
    }

    private Expr evalLambda(Cell cell, Env env) {
        Cell parameters = (Cell) cell.nth(1);
        Cell body = (Cell) cell.nthCdr(2);
        return new Closure(parameters, body, env);
    }
    
    private Expr evalLet(Cell list, Env env) {
        // (let    ((x 1)(y 2))  (+ x y))
        // ((lambda (x    y)     (+ x y)) 1 2)
        
        Cell paramArgPairs = (Cell) list.nth(1);
        Cell parameters = Cell.NIL;
        Cell args = Cell.NIL;
        for (Cell pairCell : new CellIterator(paramArgPairs)) {
            Cell pair = (Cell) pairCell.getCar();
            parameters = new Cell(pair.nth(0), parameters);
            args = new Cell(pair.nth(1), args);
        }
        
        Expr body = list.nthCdr(2);
        Cell lambdaExpr = new Cell(SYM_LAMBDA, new Cell(reverse(parameters), body));
        Cell expr = new Cell(lambdaExpr, reverse(args));

        return eval(expr, env);
    }

    private Expr evalLetrec(Cell list, Env env) {
        // (letrec ((x 1)(y 2))  (+ x y))
        // ((lambda (x    y)     (+ x y)) 1 2)
        
        Cell paramArgPairs = (Cell) list.nth(1);
        Cell parameters = Cell.NIL;
        Cell args = Cell.NIL;
        for (Cell pairCell : new CellIterator(paramArgPairs)) {
            Cell pair = (Cell) pairCell.getCar();
            parameters = new Cell(pair.nth(0), parameters);
            args = new Cell(pair.nth(1), args);
        }
        args = reverse(args);
        
        Cell body = (Cell) list.nthCdr(2);
        Cell lambdaExpr = new Cell(SYM_LAMBDA, new Cell(reverse(parameters), body));

        Env extEnv = env.extendEnv();
        Cell values = evalList(args, extEnv);
        bindParameters(extEnv, parameters, values);
        
        Cell expr = new Cell(lambdaExpr, args);
        return eval(expr, extEnv);
    }
    
    private Expr evalIf(Cell list, Env env) {
        Expr cond = list.nth(1);
        Expr trueExpr = list.nth(2);
        Expr elseExpr = list.nth(3);
        
        if (!isFalseExpr(eval(cond, env))) {
            return eval(trueExpr, env);
        }
        if (elseExpr != null) {
            return eval(elseExpr, env);
        }
        return Symbol.FALSE;
    }

    private Expr evalCond(Cell list, Env env) {
        Cell ifList = condToIf((Cell) list.getCdr());
        return evalIf(ifList, env);
    }

    private Cell condToIf(Cell list) {
        if (list == Cell.NIL) {
            return Cell.NIL;
        }
        Cell elem = (Cell) list.getCar();
        Expr pred = elem.getCar();
        if (pred == SYM_ELSE) {
            pred = Symbol.TRUE;
        }
        Expr expr = elem.nth(1);
        Expr follow = condToIf((Cell) list.getCdr());
        return (follow != Cell.NIL) ?
                Cell.list(SYM_IF, pred, expr, follow) :
                    Cell.list(SYM_IF, pred, expr);
    }
    
    private Expr evalDefine(Cell list, Env env) {
        Expr arg1 = list.nth(1);

        if (arg1 instanceof Cell) {
            return evalDefineFunction(list, env);
        }
        
        // (define symbol value)
        Symbol symbol = (Symbol) arg1;
        Expr value = list.nth(2);
        env.bind(symbol, eval(value, env));
        return symbol;
    }

    private Expr evalDefineFunction(Cell list, Env env) {
        // (define      (func    x y) (+ x y))
        // (define func (lambda (x y) (+ x y))
        Cell signature = (Cell) list.nth(1);
        Symbol symbol = (Symbol) signature.getCar();
        Cell params = (Cell) signature.getCdr();
        Cell body = (Cell) list.nthCdr(2);
        Cell lambdaExpr = new Cell(SYM_LAMBDA, new Cell(params, body));
        
        env.bind(symbol, eval(lambdaExpr, env));
        
        return symbol;
    }
    
    private Expr evalSetq(Cell list, Env env) {
        Symbol symbol = (Symbol) list.nth(1);
        if (env.getValue(symbol) == null) {
            throw new ScmRuntimeException(
                    "Undefined variable: " + symbol.getName());
        }
        Expr value = eval(list.nth(2), env);
        env.set(symbol, value);
        return value;
    }

    private Cell evalList(Expr expr, Env env) {
        LinkedList<Expr> values = new LinkedList<>();
        for (Cell cell : new CellIterator(expr)) {
            values.addFirst(eval(cell.getCar(), env));
        }

        Cell cell = Cell.NIL;
        for (Expr value : values) {
            cell = new Cell(value, cell);
        }
        return cell;
    }
    
    private Cell reverse(Cell list) {
        Cell cell = Cell.NIL;
        for (Cell c : new CellIterator(list)) {
            cell = new Cell(c.getCar(), cell);
        }
        return cell;
    }
    
    private Expr apply(Function func, Expr args) {
        if (func instanceof BuiltinFunction) {
            return ((BuiltinFunction) func).apply(args, globalEnv);
        }
        if (func instanceof Closure) {
            return applyClosure((Closure) func, args);
        }
        throw new ScmRuntimeException("Unknown function: " + func);
    }
    
    private Expr applyClosure(Closure closure, Expr args) {
        Cell parameters = closure.getParameters();
        Cell body = closure.getBody();
        Env closureEnv = closure.getEnv();
        Env env = closureEnv.extendEnv();
        
        bindParameters(env, parameters, (Cell) args);
        
        CellIterator itExpr = new CellIterator(body);
        Expr result = Cell.NIL;
        while (itExpr.hasNext()) {
            result = eval(itExpr.next().getCar(), env);
        }
        return result;
    }
    
    private void bindParameters(Env env, Cell parameters, Cell args) {
        CellIterator itParams = new CellIterator(parameters);
        CellIterator itArgs = new CellIterator(args);

        while (itParams.hasNext() && itArgs.hasNext()) {
            env.bind((Symbol) itParams.next().getCar(), itArgs.next().getCar());
        }
    }
    
    private Expr symbolValue(Symbol symbol, Env env) {
        Expr expr = env.getValue(symbol);
        if (expr == null) {
            throw new ScmRuntimeException("Unbound variable: " + symbol);
        }
        return expr;
    }
    
    private boolean isList(Expr expr) {
        return expr instanceof Cell;
    }
    
    private boolean isImmidiate(Expr expr) {
        return expr instanceof Num;
    }

    private boolean isClosure(Expr expr) {
        return expr instanceof Closure;
    }

    private boolean isSymbol(Expr expr) {
        return expr instanceof Symbol;
    }

    private boolean isFalseExpr(Expr expr) {
        return expr == Symbol.FALSE;
    }
    
    private boolean isSpecialForm(Expr expr) {
        return carMatches(expr, SYM_QUOTE)
                || carMatches(expr, SYM_LAMBDA)
                || carMatches(expr, SYM_LET)
                || carMatches(expr, SYM_LETREC)
                || carMatches(expr, SYM_IF)
                || carMatches(expr, SYM_COND)
                || carMatches(expr, SYM_DEFINE)
                || carMatches(expr, SYM_SETQ);
    }
    
    private boolean carMatches(Expr cell, Expr value) {
        return cell instanceof Cell && cell != Cell.NIL
                && ((Cell) cell).getCar() == value;
    }
}
