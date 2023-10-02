package ast;

public class VarAssign extends Stmt {

    final VarDecl vd;
    final Expr expr;

    public VarAssign(VarDecl vd, Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.vd = vd;
    }

    public Expr getExpr() {
        return expr;
    }

    public VarDecl getVarDecl() {
        return vd;
    }

    @Override
    public String toString() {
        return vd + " = " + expr + ";";
    }
}
