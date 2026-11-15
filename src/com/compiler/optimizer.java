package com.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Optimizer {
    private final Map<String, String> constants = new HashMap<>();

    public List<String> optimize(List<String> irCode) {
        List<String> optimized = new ArrayList<>();

        for (String line : irCode) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            // Labels, print, return, ifFalse -> keep, but replace temps with constants
            if (line.endsWith(":") || line.startsWith("print ") || line.startsWith("return")) {
                optimized.add(replaceConstants(line));
                continue;
            }

            if (line.startsWith("ifFalse ")) {
                optimized.add(replaceConstants(line));
                continue;
            }

            // Assignment: dest = ...
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String dest = parts[0].trim();
                String rhs = parts[1].trim();
                String[] tokens = rhs.split(" ");

                // Case 1: t1 = 3 * 2 -> Constant Folding
                if (tokens.length == 3 && isNumber(tokens[0]) && isNumber(tokens[2])) {
                    double left = Double.parseDouble(tokens[0]);
                    double right = Double.parseDouble(tokens[2]);
                    String op = tokens[1];
                    double result = switch (op) {
                        case "+" -> left + right;
                        case "-" -> left - right;
                        case "*" -> left * right;
                        case "/" -> right != 0 ? left / right : 0;
                        case "==" -> left == right ? 1 : 0;
                        case "!=" -> left != right ? 1 : 0;
                        case "<" -> left < right ? 1 : 0;
                        case ">" -> left > right ? 1 : 0;
                        case "<=" -> left <= right ? 1 : 0;
                        case ">=" -> left >= right ? 1 : 0;
                        default -> 0;
                    };
                    String val = formatNumber(result);
                    constants.put(dest, val);
                    optimized.add(dest + " = " + val + "   ; folded: " + rhs);
                    continue;
                }

                // Case 2: t1 = a + b where a and b are known constants
                if (tokens.length == 3 && constants.containsKey(tokens[0]) && constants.containsKey(tokens[2])) {
                    // Replace and re-fold in next pass - simple version: just replace
                    String newLine = dest + " = " + constants.get(tokens[0]) + " " + tokens[1] + " "
                            + constants.get(tokens[2]);
                    // Recursively fold it
                    optimized.addAll(optimize(List.of(newLine)));
                    continue;
                }

                // Case 3: x = t1 where t1 is constant -> Copy Propagation
                if (tokens.length == 1 && constants.containsKey(tokens[0])) {
                    String val = constants.get(tokens[0]);
                    constants.put(dest, val);
                    optimized.add(dest + " = " + val + "   ; propagated");
                    continue;
                }

                // Case 4: x = 5 -> remember it
                if (tokens.length == 1 && isNumber(tokens[0])) {
                    constants.put(dest, tokens[0]);
                }

                // Default: keep line, but replace known temps
                optimized.add(replaceConstants(line));
            } else {
                optimized.add(line);
            }
        }
        return optimized;
    }

    private String replaceConstants(String line) {
        for (Map.Entry<String, String> e : constants.entrySet()) {
            // Only replace whole words, not part of other temps
            // e.g. t1 should not replace t10
            line = line.replaceAll("\\b" + e.getKey() + "\\b", e.getValue());
        }
        return line;
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatNumber(double d) {
        if (d == Math.floor(d))
            return String.valueOf((int) d);
        return String.valueOf(d);
    }

    public void print(List<String> before, List<String> after) {
        System.out.println("--- Optimization ---");
        System.out.println("Before: " + before.size() + " lines, After: " + after.size() + " lines");
        for (String line : after) {
            System.out.println(line);
        }
    }
