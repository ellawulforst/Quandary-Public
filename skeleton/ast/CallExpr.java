package ast;

public class CallExpr extends Expr {

    final Ident ident;
    final ExprList el;

    public CallExpr(Ident ident, ExprList el, Location loc) {
        super(loc);
        this.ident = ident;
        this.el = el;
    }

    public Expr getCallTo() {
        return ident;
    }
    
    public Expr getParams() {
        return el;
    }

    @Override
    public String toString() {
        return ident + "(" + el + ")";
    }
}
