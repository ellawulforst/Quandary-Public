package ast;

public class UnaryExpr extends BinaryExpr {

    public static final int UMINUS = 1;

    final Expr expr1;
    final int operator;

    public UnaryExpr(Expr expr1, int operator, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
    }

    public Expr getExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case UMINUS: s = "-"; break;
        }
        return "(" + s + " " + expr1 + ")";
    }
}
