package com.compiler;

public enum TokenType {
    // Single-char
    LPAREN, RPAREN, LBRACE, RBRACE, SEMI,
    PLUS, MINUS, MUL, DIV, ASSIGN, LT, GT,
    EQ_EQ,

    // Literals
    IDENTIFIER, INT_LITERAL, FLOAT_LITERAL,

    // Keywords
    KW_INT, KW_FLOAT, KW_IF, KW_ELSE, KW_WHILE, KW_PRINT,

    EOF
}