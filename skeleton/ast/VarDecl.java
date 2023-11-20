package ast;

public class VarDecl extends ASTNode {

    final Type type;
    final String ident;
    final boolean isMutable;

    public VarDecl(boolean isMutable, Type type, String ident, Location loc) {
        super(loc);
        this.type = type;
        this.ident = ident;
        this.isMutable = isMutable;
    }

    public Type getType() {
        return this.type;
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
        switch (this.type.getType()) {
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
