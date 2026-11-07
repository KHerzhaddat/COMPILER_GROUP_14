package com.compiler;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        while (!isAtEnd()) {
            declaration();
        }
        System.out.println("Parse OK - Syntax is valid!");
    }

    private void declaration() {
        try {
            if (check(TokenType.INT) || check(TokenType.FLOAT)) {
                varDeclaration();
            } else {
                statement();
            }
        } catch (ParseError e) {
            System.out.println(e.getMessage());
            synchronize();
            throw e;
        }
    }

    private void varDeclaration() {
        advance();
        consume(TokenType.IDENTIFIER, "Expect variable name after type.");
        if (match(TokenType.ASSIGN)) {
            expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
    }

    private void statement() {
        if (match(TokenType.PRINT)) {
            printStatement();
        } else if (match(TokenType.IF)) {
            ifStatement();
        } else if (match(TokenType.RETURN)) {
            returnStatement();
        } else if (match(TokenType.LBRACE)) {
            block();
        } else {
            expressionStatement();
        }
    }

    private void printStatement() {
        expression();
        consume(TokenType.SEMICOLON, "Expect ';' after print value.");
    }

    private void ifStatement() {
        consume(TokenType.LPAREN, "Expect '(' after 'if'.");
        expression();
        consume(TokenType.RPAREN, "Expect ')' after if condition.");
        statement();
    }

    private void returnStatement() {
        if (!check(TokenType.SEMICOLON)) {
            expression();
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.");
    }

    private void block() {
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            declaration();
        }
        consume(TokenType.RBRACE, "Expect '}' after block.");
    }

    private void expressionStatement() {
        expression();
        consume(TokenType.SEMICOLON, "Expect ';' after expression.");
    }

    private void expression() { equality(); }

    private void equality() {
        comparison();
        while (match(TokenType.EQ, TokenType.NEQ)) {
            comparison();
        }
    }

    private void comparison() {
        term();
        while (match(TokenType.GT, TokenType.GTE, TokenType.LT, TokenType.LTE)) {
            term();
        }
    }

    private void term() {
        factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            factor();
        }
    }

    private void factor() {
        unary();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            unary();
        }
    }

    private void unary() {
        if (match(TokenType.MINUS, TokenType.MINUS)) {
            unary();
            return;
        }
        primary();
    }

    private void primary() {
        if (match(TokenType.NUMBER, TokenType.STRING, TokenType.IDENTIFIER)) return;
        if (match(TokenType.LPAREN)) {
            expression();
            consume(TokenType.RPAREN, "Expect ')' after expression.");
            return;
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

    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private ParseError error(Token token, String message) {
        return new ParseError("[line " + token.line + "] Error at '" + token.lexeme + "': " + message);
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;
            switch (peek().type) {
                case INT: case FLOAT: case PRINT: case IF: case RETURN:
                    return;
            }
            advance();
        }
    }

    private static class ParseError extends RuntimeException {
        ParseError(String message) { super(message); }
    }

}
