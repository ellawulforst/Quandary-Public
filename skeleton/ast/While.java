package ast;

public class While extends Stmt {

    final CondExpr cond;
    final Stmt stmt;

    public While(CondExpr cond, Stmt stmt, Location loc) {
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
        return "while (" + cond + ") {\n\t" + stmt + "\n}";
    }
}
