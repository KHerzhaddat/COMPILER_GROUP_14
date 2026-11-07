package com.compiler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "tests/test2.txt";
        if (args.length > 0) path = args[0];

        String code = Files.readString(Paths.get(path));
        System.out.println("=== Source Code ===");
        System.out.println(code);
        System.out.println("\n=== Tokens ===");

        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}