package ast;

public class Ident extends Expr {

    final String ident;

    public Ident(String ident, Location loc) {
        super(loc);
        this.ident = ident;
    }

    public String getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return ident;
    }
}
