package com.compiler;
import java.util.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0, current = 0, line = 1;
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("int", TokenType.KW_INT);
        keywords.put("float", TokenType.KW_FLOAT);
        keywords.put("if", TokenType.KW_IF);
        keywords.put("else", TokenType.KW_ELSE);
        keywords.put("while", TokenType.KW_WHILE);
        keywords.put("print", TokenType.KW_PRINT);
    }
    public Scanner(String source) { this.source = source; }
    public List<Token> scanTokens() {
        while (current < source.length()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }
    private void scanToken() {
        char c = source.charAt(current++);
        switch (c) {
            case '(': addToken(TokenType.LPAREN); break;
            case ')': addToken(TokenType.RPAREN); break;
            case '{': addToken(TokenType.LBRACE); break;
            case '}': addToken(TokenType.RBRACE); break;
            case ';': addToken(TokenType.SEMI); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.MUL); break;
            case '/': addToken(TokenType.DIV); break;
            case '<': addToken(TokenType.LT); break;
            case '>': addToken(TokenType.GT); break;
            case '=':
                boolean isEq = current < source.length() && source.charAt(current) == '=';
                if(isEq) current++;
                addToken(isEq ? TokenType.EQ_EQ : TokenType.ASSIGN);
                break;
            case ' ': case '\r': case '\t': break;
            case '\n': line++; break;
            default:
                if (Character.isDigit(c)) number();
                else if (Character.isLetter(c)) identifier();
                break;
        }
    }
    private void identifier() {
        while (current < source.length() && Character.isLetterOrDigit(source.charAt(current))) current++;
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }
    private void number() {
        while (current < source.length() && Character.isDigit(source.charAt(current))) current++;
        boolean isFloat = false;
        if (current < source.length() && source.charAt(current) == '.' && current+1 < source.length() && Character.isDigit(source.charAt(current+1))) {
            isFloat = true; current++;
            while (current < source.length() && Character.isDigit(source.charAt(current))) current++;
        }
        addToken(isFloat ? TokenType.FLOAT_LITERAL : TokenType.INT_LITERAL);
    }
    private void addToken(TokenType type) { tokens.add(new Token(type, source.substring(start, current), line)); }
}