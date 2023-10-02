package ast;

public class Cond extends ASTNode {

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int NOT = 3;

    final int operator;
    final Cond cond1;
    final Cond cond2;            

    public Cond(Cond cond1, int operator, Cond cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.cond2 = cond2;
        this.operator = operator;
    }

    public Cond getLeftCond() {
        return cond1;
    }

    public Cond getRightCond() {
        return cond2;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case AND: s = "&&"; break;
            case OR: s = "||"; break;
            case NOT: s = "!"; break;
        }
        return "(" + cond1 + " " + s + " " + cond2 + ")";
    }
}
