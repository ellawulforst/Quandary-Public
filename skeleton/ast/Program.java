package ast;

import java.io.PrintStream;

public class Program extends ASTNode {

    final FuncDefList fcdl;

    public Program(FuncDefList fcdl, Location loc) {
        super(loc);
        this.fcdl = fcdl;
    }

    public FuncDefList getFuncDefList() {
        return fcdl;
    }

    public void println(PrintStream ps) {
        ps.println(fcdl.toString());        
    }
}
