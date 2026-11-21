package com.compiler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//This is the main class that runs the compiler. It reads the source code from a file, tokenizes it using the Scanner, and then parses the tokens using the Parser.
public class Main {
    public static void main(String[] args) throws Exception {
        for (int i = 1; i <= 8; i++) {
            String filename = "tests/test" + i + ".txt";
            System.out.println("\n=== Running " + filename + " ===");

            try {
                String code = Files.readString(Paths.get(filename));
                System.out.println("=== SOURCE ===");
                System.out.println(code);

                Scanner scanner = new Scanner(code);
                List<Token> tokens = scanner.scanTokens();
                System.out.println("=== TOKENS ===");
                System.out.println(tokens);

                Parser parser = new Parser(tokens);
                parser.parse();
                System.out.println("Parse OK");

            } catch (Exception e) {
                System.out.println("FAILED " + filename + " : " + e.getMessage());
            }
        }
    }
}