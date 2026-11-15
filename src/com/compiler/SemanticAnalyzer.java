package com.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SemanticAnalyzer {

    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private boolean hadError = false;

    public boolean analyze(List<Stmt> statements) {
        beginScope();
        for (Stmt s : statements) {
            if (s != null) checkStmt(s);
        }
        endScope();
        if (!hadError) {
            System.out.println("Semantic OK - No semantic errors!");
        } else {
            System.out.println("Semantic FAILED");
        }
        return !hadError;
    }

    private void checkStmt(Stmt stmt) {
        if (stmt instanceof Stmt.Var) {
            Stmt.Var v = (Stmt.Var) stmt;
            if (v.initializer != null) checkExpr(v.initializer);
            declare(v.name);
            define(v.name);
        } else if (stmt instanceof Stmt.Expression) {
            checkExpr(((Stmt.Expression) stmt).expression);
        } else if (stmt instanceof Stmt.Print) {
            checkExpr(((Stmt.Print) stmt).expression);
        } else if (stmt instanceof Stmt.Block) {
            beginScope();
            for (Stmt s : ((Stmt.Block) stmt).statements) {
                if (s != null) checkStmt(s);
            }
            endScope();
        } else if (stmt instanceof Stmt.If) {
            Stmt.If s = (Stmt.If) stmt;
            checkExpr(s.condition);
            checkStmt(s.thenBranch);
            if (s.elseBranch != null) checkStmt(s.elseBranch);
        } else if (stmt instanceof Stmt.While) {
            Stmt.While s = (Stmt.While) stmt;
            checkExpr(s.condition);
            checkStmt(s.body);
        }
    }

    private void checkExpr(Expr expr) {
        if (expr instanceof Expr.Variable) {
            Expr.Variable v = (Expr.Variable) expr;
            if (!isDefined(v.name.lexeme)) {
                error(v.name, "Undefined variable '" + v.name.lexeme + "'.");
            }
        } else if (expr instanceof Expr.Assign) {
            Expr.Assign a = (Expr.Assign) expr;
            checkExpr(a.value);
            if (!isDefined(a.name.lexeme)) {
                error(a.name, "Undefined variable '" + a.name.lexeme + "'.");
            }
        } else if (expr instanceof Expr.Binary) {
            Expr.Binary b = (Expr.Binary) expr;
            checkExpr(b.left);
            checkExpr(b.right);
        } else if (expr instanceof Expr.Unary) {
            checkExpr(((Expr.Unary) expr).right);
        } else if (expr instanceof Expr.Grouping) {
            checkExpr(((Expr.Grouping) expr).expression);
        }
    }

    private void beginScope() { scopes.push(new HashMap<>()); }
    private void endScope() { scopes.pop(); }

    private void declare(Token name) {
        if (scopes.isEmpty()) return;
        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(name.lexeme)) {
            error(name, "Variable '" + name.lexeme + "' already declared in this scope.");
        }
        scope.put(name.lexeme, false);
    }

    private void define(Token name) {
        if (scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }

    private boolean isDefined(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) return true;
        }
        return false;
    }

    private void error(Token token, String message) {
        hadError = true;
        System.out.println("[line " + token.line + "] Semantic Error: " + message);
    }
}