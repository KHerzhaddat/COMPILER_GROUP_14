package com.compiler;

import java.util.HashMap;
import java.util.Map;

public class KeywordTable {
    public static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("int", TokenType.INT);
        keywords.put("float", TokenType.FLOAT);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("return", TokenType.RETURN);
        keywords.put("print", TokenType.PRINT);
    }

    public static TokenType getKeyword(String lexeme) {
        return keywords.get(lexeme);
    }
}
