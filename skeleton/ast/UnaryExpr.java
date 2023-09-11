package ast;

public class UnaryExpr extends Expr {

    public static final int UMINUS = 1;

    final Expr expr;
    final int operator;

    public UnaryExpr(Expr expr1, int operator, Location loc) {
        super(loc);
        this.expr = expr1;
        //System.out.println("unaryexpr this.expr:" + this.expr);
        this.operator = operator;
    }

    public Expr getExpr() {
        //System.out.println("unaryexpr getExpr:" + expr);
        return expr;
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
        return "(" + s + " " + expr + ")";
    }
}
