package ast;

public class Reassign extends Stmt {

    final String ident;
    final Expr expr;

    public Reassign(String ident , Expr expr, Location loc) {
        super(loc);
        this.ident = ident;
        this.expr = expr;
    }

    public String getIdent() {
            return ident;
        }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return ident + " = " + expr + ";";
    }
}
