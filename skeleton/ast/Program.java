package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final Expr expr;
    final Return ret;

    public Program(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.ret = null;
    }

    public Program(Return ret, Location loc) {
        super(loc);
        this.ret = ret;
        this.expr = null;
    }

    public Expr getExpr() {
        return expr;
    }

    public Return getRet() {
        return ret;
    }

    public void println(PrintStream ps) {
        if (ret == null) {
            ps.println(expr);
        } else {
            ps.println(ret);
        }
        
    }
}
