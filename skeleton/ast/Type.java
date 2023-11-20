package ast;

public class Type extends ASTNode {

    public static final int INT = 1;
    public static final int REF = 2;
    public static final int Q = 3;

    final int type;

    public Type(int type, Location loc) {
        super(loc);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        String s = null;
        switch (type) {
            case INT:  s = "int"; break;
            case REF: s = "Ref"; break;
            case Q: s = "Q"; break;
        }
        return s + "";
    }
}
