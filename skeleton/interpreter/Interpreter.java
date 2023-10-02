package interpreter;

import java.io.*;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
        return evaluate(((Return) astRoot.getFuncDef().getStmtList().getStmt()).getExpr());
    }
/*
    Object evaluate(CondExpr condExpr) {
            switch (condExpr.getOperator()) {
                case CondExpr.LE: return (boolean)evaluate(condExpr.getLeftExpr()) <= (boolean)evaluate(condExpr.getRightExpr());
                case CondExpr.GE: return (boolean)evaluate(condExpr.getLeftExpr()) >= (boolean)evaluate(condExpr.getRightExpr());
                case CondExpr.EQ: return (boolean)evaluate(condExpr.getLeftExpr()) == (boolean)evaluate(condExpr.getRightExpr());
                case CondExpr.NE: return (boolean)evaluate(condExpr.getLeftExpr()) != (boolean)evaluate(condExpr.getRightExpr());
                case CondExpr.LT: return (boolean)evaluate(condExpr.getLeftExpr()) < (boolean)evaluate(condExpr.getRightExpr());
                case CondExpr.GT: return (boolean)evaluate(condExpr.getLeftExpr()) > (boolean)evaluate(condExpr.getRightExpr());
                default: throw new RuntimeException("Unhandled CondExpr operator");
            }
    }

    Object evaluate(Cond cond) {
            switch (cond.getOperator()) {
                case Cond.AND: return (boolean)evaluate(cond.getLeftCond()) && (boolean)evaluate(cond.getRightCond());
                case Cond.OR: return (boolean)evaluate(cond.getLeftCond()) || (boolean)evaluate(cond.getRightCond());
                case Cond.NOT: return !(boolean)evaluate(cond.getRightCond()); 
                default: throw new RuntimeException("Unhandled Cond operator");
            }
    }

    Object evaluate(Stmt stmt) {
        if (stmt instanceof ifStmt) {
            ifStmt ifStmt = (ifStmt) stmt;
            if (ifStmt.getCond()) {
                return evaluate(ifStmt.getStmt());
            }
            //IF COND IS FALSE, RETURN WHAT NOW?
        }else if (stmt instanceof ifElseStmt) {
            ifElseStmt ifElseStmt = (ifElseStmt)stmt;
            if (evaluate(ifElseStmt.getCond())) {
                return evaluate(ifElseStmt.getIfStmt());
            } else {
                return evaluate(ifElseStmt.getElseStmt());
            }
        } else if (stmt instanceof Print) {
            Print print = (Print)stmt;
            return system.out.println(print.toString());
        } else if (stmt instanceof Return) {
            //IDK WHAT TO DO FOR THIS, CALL EXECUTE ROOT ORRRRR?
        } else {
            throw new RuntimeException("Unhandled Stmt type");
        }
    }

    //DONT KNOW WHAT TO DO HERE
    Object evaluate(StmtList stmtList) {
        //if its just a stmt, evaluate stmt
        //if its a stmt list, recursive call to stmt list?
        if (stmtList instanceof stmt) {
            return evaluate(stmtList.getStmt());
        }else if (stmt instanceof stmtList) {
            return evaluate(stmtList.getStmtList());
        }
    }
*/

    Object evaluate(Expr expr) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        }else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return (Long)evaluate(binaryExpr.getLeftExpr()) + (Long)evaluate(binaryExpr.getRightExpr());
                case BinaryExpr.BMINUS: return (Long)evaluate(binaryExpr.getLeftExpr()) - (Long)evaluate(binaryExpr.getRightExpr());
                case BinaryExpr.MULT: return (Long)evaluate(binaryExpr.getLeftExpr()) * (Long)evaluate(binaryExpr.getRightExpr());
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            return - ((Long)evaluate(unaryExpr.getExpr()));
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
