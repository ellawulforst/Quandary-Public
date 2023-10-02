package ast;

public class FuncDef extends Expr {

    final VarDecl vd;
    final VarDecl params;
    final StmtList stmtList;

    public FuncDef(VarDecl vd, VarDecl params, StmtList stmtList, Location loc) {
        super(loc);
        this.vd = vd;
        this.params = params;
        this.stmtList = stmtList;
    }

    public VarDecl getVarDecl() {
        return vd;
    }

    public VarDecl getParams() {
        return params;
    }
    
    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return vd + " ( " + params + " )" + " {\n\t" + stmtList + "\n}";
    }
}
