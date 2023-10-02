package ast;

public class VarDecl extends ASTNode {

    public static final int INT = 1;

    final int type;
    final String ident;

    public VarDecl(int type, String ident, Location loc) {
        super(loc);
        this.type = type;
        this.ident = ident;
    }

    public int getType() {
        return type;
    }
    
    public String getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        String s = null;
        switch (type) {
            case INT:  s = "int"; break;
        }
        return s + " " + ident;
    }
}
