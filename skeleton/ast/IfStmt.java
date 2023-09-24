package ast;

public abstract class IfStmt extends Stmt {

    final Cond cond;
    final Stmt stmt;

    IfStmt(Cond cond, Stmt stmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt = stmt;
    }

    Stmt getStmt() {
        return this.stmt;
    }

    Cond getCond() {
        return this.cond;
    }
}
