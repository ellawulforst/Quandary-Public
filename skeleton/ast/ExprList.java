package ast;

public class ExprList extends Expr {

    final ExprList neel;
    final Expr expr;

    public ExprList(Expr expr, ExprList neel, Location loc) {
        super(loc);
        this.neel = neel;
        this.expr = expr;
    }

    public ExprList getNeExprList() {
        return neel;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        if (neel != null) {
            return expr + "," + neel;
        } else {
            return expr + "";
        }
        
    }
}
