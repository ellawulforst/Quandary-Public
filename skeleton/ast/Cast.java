package ast;

public class Cast extends Expr {

    final Type type;
    final Expr expr;

    public Cast(Type type, Expr expr, Location loc) {
        super(loc);
        this.type = type;
        this.expr = expr;
    }

    public Type getCastedType() {
        return type;
    }
    
    public Expr getCastedExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "(" + type + ")" + expr;
    }
}
