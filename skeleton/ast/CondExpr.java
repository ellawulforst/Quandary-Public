package ast;

public class CondExpr extends Stmt {

    public static final int LE = 1;
    public static final int GE = 2;
    public static final int EQ = 3;
    public static final int NE = 4;
    public static final int LT = 5;
    public static final int GT = 6;
    final int operator;
    final Expr expr1;
    final Expr expr2;

    public static final int AND = 7;
    public static final int OR = 8;
    public static final int NOT = 9;
    final CondExpr cond1;
    final CondExpr cond2;

    public CondExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.operator = operator;
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.cond1 = null;
        this.cond2 = null;
    }

    public CondExpr(CondExpr cond1, int operator, CondExpr cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.cond2 = cond2;
        this.operator = operator;
        this.expr1 = null;
        this.expr2 = null;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public Expr getRightExpr() {
        return expr2;
    }

    public CondExpr getLeftCond() {
        return cond1;
    }

    public CondExpr getRightCond() {
        return cond2;
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
            case AND: s = "&&"; break;
            case OR: s = "||"; break;
            case NOT: s = "!"; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
