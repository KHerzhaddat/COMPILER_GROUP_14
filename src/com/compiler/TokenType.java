// AI Disclosure: AI used for code review / debugging suggestions.
// Core code generation logic designed and implemented by Group 14.
package com.compiler;

public enum TokenType {
    // Core
    IDENTIFIER,
    NUMBER,
    STRING,
    EOF,
    UNKNOWN,

    // Keywords
    INT, FLOAT, IF, ELSE, WHILE, FOR, RETURN, PRINT, CLASS, FUN, VAR,

    // Operators
    PLUS, MINUS, MULTIPLY, DIVIDE,
    ASSIGN, EQ, NEQ, LT, GT, LTE, GTE,

    // Delimiters
    SEMICOLON, COMMA,
    LPAREN, RPAREN,
    LBRACE, RBRACE,
    LBRACKET, RBRACKET, LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE, // <- ADD THIS LINE IF MISSING
    SLASH, STAR,
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,
    // <- MAKE SURE SLASH IS HERE
}
