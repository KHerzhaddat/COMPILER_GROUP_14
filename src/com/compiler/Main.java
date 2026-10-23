package com.compiler;

public class Main {
    public static void main(String[] args) {
        String code = "int x = 10; print x;";
        Scanner scanner = new Scanner(code);
        for (Token t : scanner.scanTokens()) {
            System.out.println(t);
        }
    }
}
