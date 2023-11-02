package ast;

public class FormalDeclList extends ASTNode {

    final VarDecl vd;
    final FormalDeclList nefdl;

    public FormalDeclList(VarDecl vd, FormalDeclList nefdl, Location loc) {
        super(loc);
        this.nefdl = nefdl;
        this.vd = vd;
    }

    public VarDecl getVarDecl() {
        return vd;
    }

    public FormalDeclList getNeFormalDeclList() {
        return nefdl;
    }

    @Override
    public String toString() {
        return vd + " " + nefdl;
    }
}
