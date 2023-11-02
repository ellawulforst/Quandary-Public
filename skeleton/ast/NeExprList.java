package ast;

public class NeExprList extends Expr {

    final Expr expr;
    final NeExprList neel;

    public NeExprList(Expr expr, NeExprList neel, Location loc) {
        super(loc);
        this.expr = expr;
        this.neel = neel;
    }

    public Expr getExpr() {
        return expr;
    }

    public NeExprList getNeExprList() {
        return neel;
    }

    @Override
    public String toString() {
        return "idk";
    }
}
