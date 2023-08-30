package ast;

public class BinaryExpr extends Expr {

    public static final int PLUS = 1;
    public static final int BMINUS = 2;
    public static final int MULT = 3;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public BinaryExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case PLUS:  s = "+"; break;
            case BMINUS: s = "-"; break;
            case MULT: s = "*"; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
