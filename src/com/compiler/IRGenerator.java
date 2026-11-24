package com.compiler;
import java.util.ArrayList;
import java.util.List;

public class IRGenerator implements Stmt.Visitor<Void>, Expr.Visitor<String> {

    private final List<String> code = new ArrayList<>();
    private int tempCount = 0;
    private int labelCount = 0;

    public List<String> generate(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            stmt.accept(this);
        }
        return code;
    }

    private String newTemp() {
        return "t" + (++tempCount);
    }

    private String newLabel() {
        return "L" + (++labelCount);
    }

    // --- Statements ---

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        expression(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        String val = expression(stmt.expression);
        code.add("print " + val);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer != null) {
            String val = expression(stmt.initializer);
            code.add(stmt.name.lexeme + " = " + val);
        } else {
            code.add(stmt.name.lexeme + " = 0");
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        for (Stmt s : stmt.statements) s.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        String cond = expression(stmt.condition);
        String elseLabel = newLabel();
        String endLabel = newLabel();
        code.add("ifFalse " + cond + " goto " + elseLabel);
        stmt.thenBranch.accept(this);
        code.add("goto " + endLabel);
        code.add(elseLabel + ":");
        if (stmt.elseBranch != null) stmt.elseBranch.accept(this);
        code.add(endLabel + ":");
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        String startLabel = newLabel();
        String endLabel = newLabel();
        code.add(startLabel + ":");
        String cond = expression(stmt.condition);
        code.add("ifFalse " + cond + " goto " + endLabel);
        stmt.body.accept(this);
        code.add("goto " + startLabel);
        code.add(endLabel + ":");
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (stmt.value != null) {
            String val = expression(stmt.value);
            code.add("return " + val);
        } else {
            code.add("return");
        }
        return null;
    }

    // --- Expressions ---

    private String expression(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "0";
        return expr.value.toString();
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        String val = expression(expr.value);
        code.add(expr.name.lexeme + " = " + val);
        return expr.name.lexeme;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        String left = expression(expr.left);
        String right = expression(expr.right);
        String temp = newTemp();
        // NOTE: space after unary minus - fixes CodeGenerator bug
        String op = expr.operator.lexeme;
        code.add(temp + " = " + left + " " + op + " " + right);
        return temp;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expression(expr.expression);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        String right = expression(expr.right);
        String temp = newTemp();
        // Space is important: "t1 = - a" not "t1 = -a"
        code.add(temp + " = " + expr.operator.lexeme + " " + right);
        return temp;
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        String left = expression(expr.left);
        String right = expression(expr.right);
        String temp = newTemp();
        code.add(temp + " = " + left + " " + expr.operator.lexeme + " " + right);
        return temp;
    }
    @Override
    public String visitCallExpr(Expr.Call expr) {
        return "";
    }
}