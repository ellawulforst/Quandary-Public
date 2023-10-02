package ast;

public class StmtList extends Stmt {

    
    final Stmt stmt;
    final StmtList stmtList;

    public StmtList(Stmt stmt, StmtList stmtList, Location loc) {
        super(loc);
        this.stmt = stmt;
        this.stmtList = stmtList;
    }

    public Stmt getStmt() {
        return stmt;
    }
    
/*
   final Stmt stmt;

   public StmtList(Stmt stmt, Location loc) {
        super(loc);
        this.stmt = stmt;
    }

    public Stmt getStmt() {
        return stmt;
    }
    */

    @Override
    public String toString() {
        return null;
    }
}
