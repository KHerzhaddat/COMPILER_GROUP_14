// AI Disclosure: AI used for code review / debugging suggestions.
// Core code generation logic designed and implemented by Group 14.

package com.compiler;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<String> assembly = new ArrayList<>();

    public List<String> generate(List<String> irCode) {
        for (String line : irCode) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Label: L1:
            if (line.endsWith(":")) {
                assembly.add(line);
                continue;
            }

            // print x
            if (line.startsWith("print ")) {
                String val = line.substring(6).trim();
                assembly.add("PRINT " + val);
                continue;
            }

            // return x  /  return
            if (line.startsWith("return")) {
                String val = line.substring(6).trim();
                if (val.isEmpty()) {
                    assembly.add("RET");
                } else {
                    assembly.add("RET " + val);
                }
                continue;
            }

            // ifFalse t3 goto L1
            if (line.startsWith("ifFalse ")) {
                // ifFalse t3 goto L1 -> JZ t3, L1
                String rest = line.substring(8).trim(); // "t3 goto L1"
                String[] parts = rest.split("goto");
                String cond = parts[0].trim();
                String label = parts[1].trim();
                assembly.add("JZ " + cond + ", " + label);
                continue;
            }

            // Assignment: x = ...  OR  t1 = a + b
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String dest = parts[0].trim();
                String rhs = parts[1].trim();

                // t1 = a + b  (Binary)
                // t1 = -a    (Unary)
                // x = t1     (Copy)
                String[] tokens = rhs.split(" ");
                if (tokens.length == 3) {
                    // a + b
                    String left = tokens[0];
                    String op = tokens[1];
                    String right = tokens[2];
                    String asmOp = switch (op) {
                        case "+" -> "ADD";
                        case "-" -> "SUB";
                        case "*" -> "MUL";
                        case "/" -> "DIV";
                        case "==" -> "EQ";
                        case "!=" -> "NE";
                        case "<" -> "LT";
                        case ">" -> "GT";
                        case "<=" -> "LE";
                        case ">=" -> "GE";
                        default -> "MOV";
                    };
                    assembly.add(asmOp + " " + dest + ", " + left + ", " + right);
                } else if (tokens.length == 2) {
                    // -a or !a
                    String op = tokens[0];
                    String val = tokens[1];
                    if (op.equals("-")) {
                        assembly.add("NEG " + dest + ", " + val);
                    } else {
                        assembly.add("NOT " + dest + ", " + val);
                    }
                } } else if (rhs.startsWith("-") && rhs.length() > 1) {
    // t1 = -x  -> NEG t1, x
    String val = rhs.substring(1);
    assembly.add("NEG " + dest + ", " + val);
} else if (rhs.startsWith("!") && rhs.length() > 1) {
    // t2 = !x  -> NOT t2, x
    String val = rhs.substring(1);
    assembly.add("NOT " + dest + ", " + val);
} else {
    // x = 5  or  x = t1
    assembly.add("MOV " + dest + ", " + rhs);
}
                }
                continue;
            }

            // Fallback
            assembly.add("; " + line);
        }
        return assembly;
    }

    public void print() {
        System.out.println("--- Assembly Code ---");
        for (String line : assembly) {
            System.out.println(line);
        }
    }

    public void writeToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String line : assembly) {
                writer.write(line + "\n");
            }
            System.out.println("Output written to " + filename);
        } catch (IOException e) {
            System.out.println("Failed to write output: " + e.getMessage());
        }
    }
}
