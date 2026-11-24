// AI Disclosure: AI used for code review / debugging suggestions.
// Core code generation logic designed and implemented by Group 14.
package com.compiler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SemanticAnalyzer implements Stmt.Visitor<Void>, Expr.Visitor<Void> {

    private final Set<String> declared = new HashSet<>();
    private boolean hadError = false;

    public boolean analyze(List<Stmt> statements) {
        for (Stmt stmt : statements) {
            stmt.accept(this);
        }
        return !hadError;
    }

    private void error(Token token, String message) {
        System.err.println("[line " + token.line + "] Semantic Error: " + message);
        hadError = true;
    }

    // --- Statements ---

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.initializer != null) stmt.initializer.accept(this);
        declared.add(stmt.name.lexeme);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        for (Stmt s : stmt.statements) s.accept(this);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        stmt.condition.accept(this);
        stmt.thenBranch.accept(this);
        if (stmt.elseBranch != null) stmt.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        stmt.condition.accept(this);
        stmt.body.accept(this);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        // This was missing before - return was ignored
        if (stmt.value != null) stmt.value.accept(this);
        return null;
    }

    // --- Expressions ---

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!declared.contains(expr.name.lexeme)) {
            error(expr.name, "Undefined variable '" + expr.name.lexeme + "'.");
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        expr.value.accept(this);
        if (!declared.contains(expr.name.lexeme)) {
            error(expr.name, "Assignment to undefined variable '" + expr.name.lexeme + "'.");
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        expr.expression.accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        // Function calls not checked yet - just check the callee and args
        expr.callee.accept(this);
        for (Expr arg : expr.arguments) {
            arg.accept(this);
        }
        return null;
    }
}
