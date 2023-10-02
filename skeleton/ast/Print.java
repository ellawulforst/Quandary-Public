package ast;

import java.io.PrintStream;

public class Print extends Stmt {

    final Expr expr;

    public Print(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    public void println(PrintStream ps) {
        ps.println(expr);
    }
}
