package com.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test code if no file is given
        String sourceCode = "int x = 10 + 20; \n print x;";

        // If you pass a file: java Main input.txt
        if (args.length > 0) {
            try {
                sourceCode = new String(Files.readAllBytes(Paths.get(args[0])));
                System.out.println("Reading file: " + args[0]);
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
                return;
            }
        }

        System.out.println("=== COMPILER_GROUP_14 - Lexical Analysis ===");
        System.out.println("Source:\n" + sourceCode + "\n");

        try {
            // Assuming your Lexer class is named Lexer or Scanner
            Scanner lexer = new Scanner(sourceCode);
            List<Token> tokens = lexer.scanTokens();
            System.out.println("--- Tokens ---");
            for (Token token : tokens) {
                System.out.println(token);
            }

            System.out.println("\nTotal tokens: " + tokens.size());

        } catch (Exception e) {
            System.err.println("Compilation Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}