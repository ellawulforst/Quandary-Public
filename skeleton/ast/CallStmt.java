package ast;

public class CallStmt extends Stmt {

    final String ident;
    final ExprList el;

    public CallStmt(String ident, ExprList el, Location loc) {
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
        return ident + " ( " + el + ");";
    }
}
