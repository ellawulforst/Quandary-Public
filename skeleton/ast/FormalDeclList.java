package ast;

public class FormalDeclList extends NeFormalDeclList {

    final NeFormalDeclList nefdl;

    public FormalDeclList(NeFormalDeclList nefdl, Location loc) {
        super(loc);
        this.nefdl = nefdl;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return nefdl;
    }

    @Override
    public String toString() {
        return nefdl;
    }
}
