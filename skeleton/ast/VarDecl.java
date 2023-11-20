package ast;

public class VarDecl extends ASTNode {

    final int type;
    final String ident;
    final boolean isMutable;

    public VarDecl(boolean isMutable, int type, String ident, Location loc) {
        super(loc);
        this.type = type;
        this.ident = ident;
        this.isMutable = isMutable;
    }

    public int getType() {
        return type;
    }
    
    public String getIdent() {
        return ident;
    }

    public boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        String s = null;
        switch (type.getType()) {
            case 1:  s = "int"; break;
            case 2:  s = "Ref"; break;
            case 3:  s = "Q"; break;
        }
        if (isMutable) {
            return "mutable" + s + " " + ident;
        }
        return s + " " + ident;
    }
}
