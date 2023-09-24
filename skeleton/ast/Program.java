package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final Stmt s;

    public Program(Stmt s, Location loc) {
        super(loc);
        this.s = s;
    }

    public Stmt getStmt() {
        return s;
    }

    public void println(PrintStream ps) {
            ps.println(s);        
    }
}
