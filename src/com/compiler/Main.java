package com.compiler;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Try both locations: tests/test1.txt and test1.txt
        String filename = args.length > 0 ? args[0] : "tests/test1.txt";
        String source = null;

        String[] tryFiles = {filename, "test1.txt", "tests/test1.txt"};
        for (String f : tryFiles) {
            try {
                source = Files.readString(Path.of(f));
                filename = f;
                break;
            } catch (IOException ignored) {}
        }

        if (source == null) {
            System.out.println("Could not read file: " + filename);
            System.out.println("Looked in: tests/test1.txt and test1.txt");
            System.out.println("Current folder: " + Path.of("").toAbsolutePath());
            return;
        }

        System.out.println("=== SOURCE (" + filename + ") ===");
        System.out.println(source);

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        System.out.println("\n=== TOKENS ===");
        for (Token t : tokens) System.out.println(t);
        System.out.println("\n=== SCAN OK - " + tokens.size() + " tokens ===");
    }
}