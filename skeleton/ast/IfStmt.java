package ast;

public class IfStmt extends Stmt {

    final CondExpr cond;
    final Stmt stmt;

    public IfStmt(CondExpr cond, Stmt stmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt = stmt;
    }

    public Stmt getStmt() {
        return this.stmt;
    }

    public CondExpr getCond() {
        return this.cond;
    }

    @Override
    public String toString() {
        return "if (" + cond + ") {\n\t" + stmt + "\n}";
    }
}
