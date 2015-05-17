package com.way_nifty.strcat.scmj;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    
    private Tokenizer tokenizer;
    
    public Parser(Reader reader) {
        tokenizer = new Tokenizer(reader);
    }

    public Expr readExpr() throws IOException {
        Token token = tokenizer.next();
        
        switch (token.getKind()) {
        case EOF:
            return null;
        case SYMBOL:
            return Symbol.get(token.getText());
        case NUM:
            return new Num(Long.parseLong(token.getText()));
        case QUOTE:
            return Cell.list(Symbol.get("quote"), readExpr());
        case CELL_START:
            List<Expr> exprs = new ArrayList<>();
            while (true) {
                Token nextToken = tokenizer.next();
                if (nextToken.getKind() == TokenKind.CELL_END) {
                    break;
                }
                tokenizer.pushback(nextToken);
                Expr expr = readExpr();
                if (expr == null) {
                    throw new ScmRuntimeException("Missing ')'.");
                }
                exprs.add(expr);
            }
            return exprs.isEmpty() ? Cell.NIL : Cell.list(exprs);
        default:
            break;
        }
        throw new ScmRuntimeException("Unexpected token: " + token.getText());
    }
    
    private static enum TokenKind {
        EOF,
        SYMBOL,
        NUM,
        CELL_START,
        CELL_END,
        QUOTE
    }
    
    private static class Token {

        private TokenKind kind;
        private String text;

        public Token(TokenKind kind, String text) {
            this.kind = kind;
            this.text = text;
        }

        public TokenKind getKind() {
            return kind;
        }

        public String getText() {
            return text;
        }
    }
    
    private static class Tokenizer {
        private LineNumberReader lineReader;
        private PushbackReader reader;
        private Token nextToken = null;
        
        public Tokenizer(Reader reader) {
            this.lineReader = new LineNumberReader(reader);
            this.reader = new PushbackReader(this.lineReader);
        }
        
        public Token next() throws IOException {
            if (nextToken != null) {
                Token token = nextToken;
                nextToken = null;
                return token;
            }
            
            int startCh = skipSpaceAndReadChar();
            if (startCh == -1) {
                return new Token(TokenKind.EOF, "");
            }
            StringBuilder buf = new StringBuilder();
            buf.append((char) startCh);
            
            if (startCh == '(') {
                return new Token(TokenKind.CELL_START, buf.toString());
            }
            if (startCh == ')') {
                return new Token(TokenKind.CELL_END, buf.toString());
            }
            if (startCh == '\'') {
                return new Token(TokenKind.QUOTE, buf.toString());
            }
            
            int ch;
            while ((ch = reader.read()) != -1 && !isStopChar(ch)) {
                buf.append((char) ch);
            }
            if (ch != -1) {
                reader.unread(ch);
            }
            String text = buf.toString();
            
            if (isSymbolStart(startCh)) {
                for (int i = 1; i < text.length(); i++) {
                    if (!isSymbolPart(text.charAt(i))) {
                        throw new ScmRuntimeException("Invalid symbol: " + text);
                    }
                }
                return new Token(TokenKind.SYMBOL, text);
            } else if (isNumStart(startCh)) {
                try {
                    Long.parseLong(text);
                    return new Token(TokenKind.NUM, text);
                } catch (NumberFormatException e) {
                    throw new ScmRuntimeException("Invalid number: " + text);
                }
            }
            throw new ScmRuntimeException("Invalid token: " + text);
        }

        public void pushback(Token token) {
            nextToken = token;
        }
        
        private int skipSpaceAndReadChar() throws IOException {
            int c;
            while ((c = reader.read()) != -1) {
                if (c == ';') {
                    lineReader.readLine();
                } else if (!isSpace(c)) {
                    break;
                }
            }
            return c;
        }
        
        private boolean isSpace(int c) {
            return Character.isWhitespace(c);
        }
        
        private boolean isStopChar(int c) {
            return c == -1 || c == ';' || isSpace(c) || (
                    !isSymbolStart(c) && !isSymbolPart(c) &&
                    !isNumStart(c) && !isNumPart(c));
        }
        
        private boolean isSymbolStart(int c) {
            return Character.isAlphabetic(c)
                    || c == '!'
                    || c == '$'
                    || c == '%'
                    || c == '&'
                    || c == '*'
                    || c == '/'
                    || c == '+'
                    || c == '-'
                    || c == '.'
                    || c == ':'
                    || c == '<'
                    || c == '>'
                    || c == '='
                    || c == '?'
                    || c == '@'
                    || c == '^'
                    || c == '_'
                    || c == '~'
                    ;
        }

        private boolean isSymbolPart(int c) {
            return isSymbolStart(c) || Character.isDigit(c);
        }

        private boolean isNumStart(int c) {
            return Character.isDigit(c);
        }

        private boolean isNumPart(int c) {
            return Character.isDigit(c);
        }
    }
}
