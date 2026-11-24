// AI Disclosure: AI used for code review / debugging suggestions.
// Core code generation logic designed and implemented by Group 14.
package com.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.compiler.Main <source file>");
            System.out.println("Example: java com.compiler.Main test.txt");
            return;
        }

        String path = args[0];
        String source;
        try {
            source = Files.readString(Paths.get(path));
        } catch (IOException e) {
            System.err.println("Could not read file: " + path);
            return;
        }

        // 1. Scanner - yours is called Scanner, not Lexer
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // 2. Parser
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // 3. Semantic Analyzer
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        boolean ok = analyzer.analyze(statements);
        if (!ok) {
            System.err.println("Semantic check failed. Stopping.");
            return;
        }

        // 4. IR Generator
        IRGenerator irGen = new IRGenerator();
        List<String> ir = irGen.generate(statements);

        System.out.println("=== IR ===");
        for (String line : ir) System.out.println(line);

        // 5. Code Generator
        CodeGenerator codeGen = new CodeGenerator();
        String asm = codeGen.generate(ir);

        System.out.println("\n=== Assembly ===");
        System.out.println(asm);
    }
}
