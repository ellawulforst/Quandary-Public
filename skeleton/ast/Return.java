package ast;

import java.io.PrintStream;

public class Return extends ASTNode {

    final Expr expr;

    public Return(Expr expr, Location loc) {
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
