package ast;

public class ExprList extends NeExprList {

    final NeExprList neel;

    public ExprList(NeExprList neel, Location loc) {
        super(loc);
        this.neel = neel;
    }

    public NeExprList getNeExprList() {
        return neel;
    }

    @Override
    public String toString() {
        return neel;
    }
}
