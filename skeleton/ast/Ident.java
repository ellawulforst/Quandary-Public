package ast;

public class Ident extends Expr {

    final Object ident;

    public Ident(String ident, Location loc) {
        super(loc);
        this.ident = ident;
    }

    public Object getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return ident.toString();
    }
}
