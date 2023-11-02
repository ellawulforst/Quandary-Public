package ast;

public class FuncDefList extends ASTNode {

    final FuncDef fcd;
    final FuncDefList fcdl;

    public FuncDefList(FuncDef fcd, FuncDefList fcdl, Location loc) {
        super(loc);
        this.fcd = fcd;
        this.fcdl = fcdl;
    }

    public FuncDef getFuncDef() {
        return fcd;
    }

    public FuncDefList getFuncDefList() {
        return fcdl;
    }

    @Override
    public String toString() {
        return fcd + "\n" + fcdl;
        
    }
}
