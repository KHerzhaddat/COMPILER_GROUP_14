package com.compiler;

import java.util.ArrayList;
import java.util.List;

public class IRGenerator {
    private final List<String> code = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;

    public List<String> generate(List<Stmt> statements) {
        for (Stmt s : statements) {
            if (s != null) genStmt(s);
        }
        return code;
    }

    public void print() {
        System.out.println("--- IR Code ---");
        for (String line : code) {
            System.out.println(line);
        }
    }

    private String newTemp() {
        tempCount++;
        return "t" + tempCount;
    }

    private String newLabel() {
        labelCount++;
        return "L" + labelCount;
    }

    private void genStmt(Stmt stmt) {
        if (stmt instanceof Stmt.VarDecl) {
            Stmt.VarDecl v = (Stmt.VarDecl) stmt;
            if (v.initializer != null) {
                String result = genExpr(v.initializer);
                code.add(v.name.lexeme + " = " + result);
            } else {
                code.add(v.name.lexeme + " = 0");
            }

        } else if (stmt instanceof Stmt.Print) {
            String result = genExpr(((Stmt.Print) stmt).expression);
            code.add("print " + result);

        } else if (stmt instanceof Stmt.ExprStmt) {
            genExpr(((Stmt.ExprStmt) stmt).expression);

        } else if (stmt instanceof Stmt.If) {
            Stmt.If i = (Stmt.If) stmt;
            String cond = genExpr(i.condition);
            String endLabel = newLabel();
            code.add("ifFalse " + cond + " goto " + endLabel);
            genStmt(i.thenBranch);
            code.add(endLabel + ":");

        } else if (stmt instanceof Stmt.Block) {
            for (Stmt s : ((Stmt.Block) stmt).statements) {
                if (s != null) genStmt(s);
            }

        } else if (stmt instanceof Stmt.Return) {
            Stmt.Return r = (Stmt.Return) stmt;
            if (r.value != null) {
                String result = genExpr(r.value);
                code.add("return " + result);
            } else {
                code.add("return");
            }
        }
    }

    // Returns the name of the temp / variable holding the result
    private String genExpr(Expr expr) {
        if (expr instanceof Expr.Literal) {
            Object v = ((Expr.Literal) expr).value;
            if (v instanceof Double) {
                double d = (Double) v;
                // Print 5 not 5.0 if it's a whole number
                if (d == Math.floor(d)) {
                    return String.valueOf((int) d);
                }
                return String.valueOf(d);
            }
            if (v instanceof String) {
                return "\"" + v + "\"";
            }
            return String.valueOf(v);

        } else if (expr instanceof Expr.Variable) {
            return ((Expr.Variable) expr).name.lexeme;

        } else if (expr instanceof Expr.Grouping) {
            return genExpr(((Expr.Grouping) expr).expression);

        } else if (expr instanceof Expr.Unary) {
            Expr.Unary u = (Expr.Unary) expr;
            String right = genExpr(u.right);
            String temp = newTemp();
            code.add(temp + " = " + u.operator.lexeme + right);
            return temp;

        } else if (expr instanceof Expr.Binary) {
            Expr.Binary b = (Expr.Binary) expr;
            String left = genExpr(b.left);
            String right = genExpr(b.right);
            String temp = newTemp();
            code.add(temp + " = " + left + " " + b.operator.lexeme + " " + right);
            return temp;
        }
        return "0";
    }
}