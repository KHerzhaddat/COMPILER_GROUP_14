package com.compiler;

import java.util.ArrayList;
import java.util.List;

import static com.compiler.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private boolean hadError = false;

    public Parser(List<Token> tokens) { this.tokens = tokens; }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            Stmt s = declaration();
            if (s != null) statements.add(s);
        }
        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(INT, FLOAT)) return varDeclaration();
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        if (match(ASSIGN)) initializer = expression();
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if (match(PRINT)) return printStatement();
        if (match(LBRACE)) return new Stmt.Block(block());
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt ifStatement() {
        consume(LPAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RPAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) elseBranch = statement();
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(LPAREN, "Expect '(' after 'while'.");
        Expr condition = expression();
        consume(RPAREN, "Expect ')' after condition.");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }


    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RBRACE) && !isAtEnd()) {
            Stmt s = declaration();
            if (s != null) statements.add(s);
        }
        consume(RBRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr expression() { return assignment(); }

    private Expr assignment() {
        Expr expr = equality();
        if (match(ASSIGN)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                return new Expr.Assign(((Expr.Variable) expr).name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(NEQ, EQ)) {
            Token op = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(GT, GTE, LT, LTE)) {
            Token op = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr term() {
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token op = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();
        while (match(DIVIDE, MULTIPLY)) {
            Token op = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(MINUS)) {
            Token op = previous();
            return new Expr.Unary(op, unary());
        }
        return primary();
    }

    private Expr primary() {
        if (match(NUMBER)) return new Expr.Literal(Double.parseDouble(previous().lexeme));
        if (match(STRING)) return new Expr.Literal(previous().lexeme);
        if (match(IDENTIFIER)) return new Expr.Variable(previous());
        if (match(LPAREN)) {
            Expr expr = expression();
            consume(RPAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (check(t)) { advance(); return true; }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() { return peek().type == EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private ParseError error(Token token, String message) {
        hadError = true;
        System.out.println("[line " + token.line + "] Parse Error: " + message);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case INT: case FLOAT: case FOR:
                case IF: case WHILE: case PRINT: case RETURN:
                    return;
            }
            advance();
        }
    }

    public boolean hadError() { return hadError; }
    private static class ParseError extends RuntimeException {}
}