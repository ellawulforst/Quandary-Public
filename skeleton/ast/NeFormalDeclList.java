package ast;

public class NeFormalDeclList extends VarDecl {

    final VarDecl vd;
    final NeFormalDeclList nefdl;

    public NeFormalDeclList(VarDecl vd, NeFormalDeclList nefdl, Location loc) {
        super(loc);
        this.vd = vd;
        this.nefdl = nefdl;
    }

    public VarDecl getVarDecl() {
        return vd;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return nefdl;
    }

    @Override
    public String toString() {
        if (nefdl == null) {
            return vd;
        } else {
            return vd + "," + nefdl;
        }
    }
}
