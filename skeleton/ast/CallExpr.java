package ast;

public class CallExpr extends Expr {

    final String ident;
    final ExprList el;

    public CallExpr(String ident, ExprList el, Location loc) {
        super(loc);
        this.ident = ident;
        this.el = el;
    }

    public String getCallTo() {
        return ident;
    }
    
    public ExprList getParams() {
        return el;
    }

    @Override
    public String toString() {
        return ident + "(" + el + ")";
    }
}
