package com.compiler;

public enum TokenType {
    // Core
    IDENTIFIER,
    NUMBER,
    STRING,
    EOF,
    UNKNOWN,

    // Keywords
    INT, FLOAT, IF, ELSE, WHILE, FOR, RETURN, PRINT,

    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE,
    ASSIGN, EQ, NEQ, LT, GT, LTE, GTE,

    // Delimiters
    SEMICOLON, COMMA,
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LBRACKET, RBRACKET
}