package ast;

public abstract class IfElseStmt extends Stmt {

    final Cond cond;
    final Stmt ifStmt;
    final Stmt elseStmt;

    IfElseStmt(Cond cond, Stmt ifStmt, Stmt elseStmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    Stmt getIfStmt() {
        return this.ifStmt;
    }

    Stmt getElseStmt() {
        return this.elseStmt;
    }

    Cond getCond() {
        return this.cond;
    }
    
    @Override
    public String toString() {
        return "if (" + cond + ") {" + ifStmt + "}" + "else {" + elseStmt + "}";
    }
}
