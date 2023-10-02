package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final FuncDef fcd;

    public Program(FuncDef fcd, Location loc) {
        super(loc);
        this.fcd = fcd;
    }

    public FuncDef getFuncDef() {
        return fcd;
    }

    public void println(PrintStream ps) {
        ps.println(fcd.toString());        
    }
}
