package com.compiler;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 1;
    private int tokenCol = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            tokenCol = column;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LPAREN); break;
            case ')': addToken(TokenType.RPAREN); break;
            case '{': addToken(TokenType.LBRACE); break;
            case '}': addToken(TokenType.RBRACE); break;
            case '[': addToken(TokenType.LBRACKET); break;
            case ']': addToken(TokenType.RBRACKET); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ',': addToken(TokenType.COMMA); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.MULTIPLY); break;
            case '/':
                if (match('/')) { // Single line comment
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) { // Multi-line comment
                    while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
                        if (peek() == '\n') { line++; column = 0; }
                        advance();
                    }
                    if (!isAtEnd()) { advance(); advance(); } // consume */
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;
            case '=':
                addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN);
                break;
            case '!':
                addToken(match('=') ? TokenType.NEQ : TokenType.UNKNOWN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LTE : TokenType.LT);
                break;
            case '>':
                addToken(match('=') ? TokenType.GTE : TokenType.GT);
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++; column = 0;
                break;
            case '"': scanString(); break;
            default:
                if (isDigit(c)) {
                    scanNumber();
                } else if (isAlpha(c)) {
                    scanIdentifier();
                } else {
                    System.err.println("Lexical Error at Line " + line + " Col " + tokenCol + ": Unexpected character '" + c + "'");
                    addToken(TokenType.UNKNOWN);
                }
                break;
        }
    }

    private void scanIdentifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = KeywordTable.getKeyword(text);
        if (type == null) type = TokenType.IDENTIFIER;
        addToken(type, text);
    }

    private void scanNumber() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(TokenType.NUMBER, source.substring(start, current));
    }

    private void scanString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') { line++; column = 0; }
            advance();
        }
        if (isAtEnd()) {
            System.err.println("Lexical Error at Line " + line + ": Unterminated string.");
            return;
        }
        advance(); // closing "
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++; column++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) { return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'; }
    private boolean isAlphaNumeric(char c) { return isAlpha(c) || isDigit(c); }
    private boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    private boolean isAtEnd() { return current >= source.length(); }
    private char advance() { current++; column++; return source.charAt(current - 1); }
    private void addToken(TokenType type) { addToken(type, source.substring(start, current)); }
    private void addToken(TokenType type, String lexeme) { tokens.add(new Token(type, lexeme, line, tokenCol)); }
}