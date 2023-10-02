package ast;

public class CondExpr extends ASTNode {

    public static final int LE = 1;
    public static final int GE = 2;
    public static final int EQ = 3;
    public static final int NE = 4;
    public static final int LT = 5;
    public static final int GT = 6;

    final int operator;
    final Expr expr1;
    final Expr expr2;

    public CondExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.operator = operator;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public Expr getRightExpr() {
        return expr2;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case LE: s = "<="; break;
            case GE: s = ">="; break;
            case EQ: s = "=="; break;
            case NE: s = "!="; break;
            case LT: s = "<"; break;
            case GT: s = ">"; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
