package ast;

public class IfElseStmt extends Stmt {

    final CondExpr cond;
    final Stmt ifStmt;
    final Stmt elseStmt;

    public IfElseStmt(CondExpr cond, Stmt ifStmt, Stmt elseStmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public Stmt getIfStmt() {
        return this.ifStmt;
    }

    public Stmt getElseStmt() {
        return this.elseStmt;
    }

    public CondExpr getCond() {
        return this.cond;
    }
    
    @Override
    public String toString() {
        return "if (" + cond + ") {\n\t" + ifStmt + "\n}" + "else {\n\t" + elseStmt + "\n}";
    }
}
