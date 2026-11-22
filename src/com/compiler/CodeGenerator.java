// AI Disclosure: AI used for code review / debugging suggestions.
// Core code generation logic designed and implemented by Group 14.

package com.compiler;

import java.util.List;

public class CodeGenerator {

    private final StringBuilder out = new StringBuilder();

    public String generate(List<String> ir) {
        out.setLength(0);

        for (String line : ir) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Label: L1:
            if (line.endsWith(":")) {
                out.append(line).append("\n");
                continue;
            }

            // print x
            if (line.startsWith("print ")) {
                String val = line.substring(6).trim();
                out.append("PRINT ").append(val).append("\n");
                continue;
            }

            // return x / return
            if (line.equals("return") || line.startsWith("return ")) {
                out.append("RET ").append(line.substring(6).trim()).append("\n");
                continue;
            }

            // ifFalse cond goto L1
            if (line.startsWith("ifFalse ")) {
                // format: ifFalse cond goto L1
                String[] parts = line.split("\\s+");
                // parts[0]=ifFalse, parts[1]=cond, parts[2]=goto, parts[3]=label
                out.append("JZ ").append(parts[1]).append(", ").append(parts[3]).append("\n");
                continue;
            }

            // goto L1
            if (line.startsWith("goto ")) {
                out.append("JMP ").append(line.substring(5).trim()).append("\n");
                continue;
            }

            // Assignment: dest = rhs
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String dest = parts[0].trim();
                String rhs = parts[1].trim();

                String[] tokens = rhs.split("\\s+");

                if (tokens.length == 1) {
                    // x = 5  or  x = y
                    out.append("MOV ").append(dest).append(", ").append(tokens[0]).append("\n");
                } else if (tokens.length == 2) {
                    // x = - a  or  x = ! a  (IRGenerator now emits a space)
                    // tokens[0] = -, tokens[1] = a
                    if (tokens[0].equals("-")) {
                        out.append("NEG ").append(dest).append(", ").append(tokens[1]).append("\n");
                    } else if (tokens[0].equals("!")) {
                        out.append("NOT ").append(dest).append(", ").append(tokens[1]).append("\n");
                    } else {
                        out.append("MOV ").append(dest).append(", ").append(rhs).append("\n");
                    }
                } else if (tokens.length == 3) {
                    // x = a + b
                    // tokens[0]=a, tokens[1]=op, tokens[2]=b
                    String op = tokens[1];
                    String instr = switch (op) {
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
                        case "and" -> "AND";
                        case "or" -> "OR";
                        default -> "MOV";
                    };
                    out.append(instr).append(" ").append(dest)
                            .append(", ").append(tokens[0])
                            .append(", ").append(tokens[2]).append("\n");
                } else {
                    // Fallback for old format: x = -a (no space)
                    if (rhs.startsWith("-") && rhs.length() > 1) {
                        out.append("NEG ").append(dest).append(", ").append(rhs.substring(1)).append("\n");
                    } else if (rhs.startsWith("!") && rhs.length() > 1) {
                        out.append("NOT ").append(dest).append(", ").append(rhs.substring(1)).append("\n");
                    } else {
                        out.append("MOV ").append(dest).append(", ").append(rhs).append("\n");
                    }
                }
            }
        }
        return out.toString();
    }
}