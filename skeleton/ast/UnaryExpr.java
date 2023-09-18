package ast;

public class UnaryExpr extends Expr {

    final Expr expr;

    public UnaryExpr(Expr expr1, Location loc) {
        super(loc);
        this.expr = expr1;
        //System.out.println("unaryexpr this.expr:" + this.expr);
    }

    public Expr getExpr() {
        //System.out.println("unaryexpr getExpr:" + expr);
        return expr;
    }

    @Override
    public String toString() {
        return "(-" + expr + ")";
    }
}
