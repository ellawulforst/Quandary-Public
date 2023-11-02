package ast;

public class FuncDef extends Expr {

    final VarDecl vd;
    final FormalDeclList fdl;
    final StmtList stmtList;

    public FuncDef(VarDecl vd, FormalDeclList fdl, StmtList stmtList, Location loc) {
        super(loc);
        this.vd = vd;
        this.fdl = fdl;
        this.stmtList = stmtList;
    }

    public String getFuncName() {
        return vd.getIdent();
    }

    public VarDecl getVarDecl() {
        return vd;
    }

    public FormalDeclList getFormalDeclList() {
        return fdl;
    }
    
    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        returnvd + "(" + fdl + ")" + "\n{" + stmtList + "}";
    }
}
