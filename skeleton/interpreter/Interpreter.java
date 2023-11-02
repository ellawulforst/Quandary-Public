package interpreter;

import java.io.*;
import java.util.Random;
import java.util.*;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    Map<Ident, FuncDef> funcs = new HashMap<Ident, FuncDef>();

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

    Object executeRoot(Program astRoot, long[] args) {
        FuncDef mainFunc;
        FuncDefList funcList = astRoot.getFuncDefList();
        FuncDef func = funcList.getFuncDef();
        
        while (func != null) {
            if (func.getFuncName().equals("main")) {
                mainFunc = func.getNextInList();
            }
            funcs.put(func.getFuncName(), func);
        }
        return runFunc(mainFunc, args);
    }

    Object runFunc(FuncDef fd, long[] args) { //HOW DO I PASS IN THE FORMAL ARG VALUES?
        Map<String, Long> scopeVars = new HashMap<String, Long>();
        Map<String, Long> cmdArgs = new HashMap<String, Long>();
        
        //if there are no params, fd has an empty decl list, dont need to do anything
        //if multiple params, need to loop through decl list in the func def and add each
        FormalDeclList declList = fd.getFormalDeclList();
        if (declList instanceof NeFormalDeclList) {
            cmdArgs.put(declList.getNeFormalDeclList().getVarDecl().getIdent(), args[0]);
            NeFormalDeclList nextDecl = declList.getNeFormalDeclList().getNeFormalDeclList();
            int i = 1;
            while (nextDecl != null) //HOW TO TELL IF THERE IS A NEXT DECL? {
                cmdArgs.put(nextDecl.getVarDecl().getIdent(), args[x++]);
                nextDecl = nextDecl.getNeFormalDeclList();
            }
        }
        
        StmtList stmtList = fd.getStmtList();
        
        while (stmtList != null) {
            Object ret = evaluate(stmtList.getStmt(), scopeVars, cmdArgs);
            
            if (ret instanceof VarAssign) {
                VarAssign varAssign = (VarAssign) ret;
                String ident = varAssign.getVarDecl().getIdent();
                long value = (long) evaluate(varAssign.getExpr(), scopeVars, cmdArgs);
                if(scopeVars.containsKey(ident)) {
                    fatalError("wrong", 222);
                } else {
                    scopeVars.put(ident, value);
                }
            } else if (ret instanceof Return) {
                Return ret2 = (Return) ret;
                return evaluate(ret2.getExpr(), scopeVars, cmdArgs);
            }
                stmtList = stmtList.getStmtList();
        }
        return null;
    }

    Object evaluate(Stmt stmt, Map<String, Long> currVars, Map<String, Long> parVars) {
        //will either return a return or a variable declaration
        if (stmt instanceof VarAssign || stmt instanceof Return) {
            return stmt;
        } else if (stmt instanceof StmtList) {
                StmtList stmtList = (StmtList) stmt;
                Map<String, Long> newParVars = new HashMap<String, Long>(parVars);
                newParVars.putAll(currVars);
                Map<String, Long> subVars = new HashMap<String, Long>();
                while (stmtList != null) {
                    Object ret = evaluate(stmtList.getStmt(), subVars, newParVars);
                    if (ret instanceof VarAssign) {
                        VarAssign varAssign = (VarAssign) ret;
                        String ident = varAssign.getVarDecl().getIdent();
                        long value = (long) evaluate(varAssign.getExpr(), subVars, newParVars);
                        if(subVars.containsKey(ident)) {
                            fatalError("wrong", 222);
                        }
                        if(newParVars.containsKey(ident)) {
                            fatalError("wrong", 222);
                        } else {
                            subVars.put(ident, value);
                        }
                    } else if (ret instanceof Return) {
                        Return ret2 = (Return) ret;
                        Expr retConst = new ConstExpr((Long) evaluate(ret2.getExpr(), subVars, newParVars), null);
                        Stmt retVar = new Return(retConst, null);
                        return retVar;
                    }
                    stmtList = stmtList.getStmtList();
                }
                return stmt;
        } else if (stmt instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) stmt;
                Boolean cond = evaluate(ifStmt.getCond(), currVars, parVars);
                if (cond) {
                    return evaluate(ifStmt.getStmt(), currVars, parVars);
                }
        } else if (stmt instanceof IfElseStmt) {
                IfElseStmt ifElseStmt = (IfElseStmt) stmt;
                Boolean cond = evaluate(ifElseStmt.getCond(), currVars, parVars);
                if (cond) {
                    return evaluate(ifElseStmt.getIfStmt(), currVars, parVars);
                } else {
                    return evaluate(ifElseStmt.getElseStmt(), currVars, parVars);
                }
        } else if (stmt instanceof Print) {
                Print print = (Print) stmt;
                System.out.println(evaluate(print.getExpr(), currVars, parVars));
        }
            return null;
        
    }

    Boolean evaluate(CondExpr condExpr, Map<String, Long> currVars, Map<String, Long> parVars) {
            switch (condExpr.getOperator()) {
                case CondExpr.LE: return (boolean)((long)evaluate(condExpr.getLeftExpr(), currVars, parVars) <= (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.GE: return (boolean)((long)evaluate(condExpr.getLeftExpr(), currVars, parVars) >= (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.EQ: return (boolean)((long) evaluate(condExpr.getLeftExpr(), currVars, parVars) == (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.NE: return (boolean)((long) evaluate(condExpr.getLeftExpr(), currVars, parVars) != (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.LT: return (boolean)((long) evaluate(condExpr.getLeftExpr(), currVars, parVars) < (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.GT: return (boolean)((long) evaluate(condExpr.getLeftExpr(), currVars, parVars) > (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
                case CondExpr.AND: return (boolean)evaluate(condExpr.getLeftCond(), currVars, parVars) && (boolean)evaluate(condExpr.getRightCond(), currVars, parVars);
                case CondExpr.OR: return (boolean)evaluate(condExpr.getLeftCond(), currVars, parVars) || (boolean)evaluate(condExpr.getRightCond(), currVars, parVars);
                case CondExpr.NOT: return !(boolean)evaluate(condExpr.getRightCond(), currVars, parVars); 
                default: throw new RuntimeException("Unhandled CondExpr operator");
            }
    }

    Object evaluate(Expr expr, Map<String, Long> currVars, Map<String, Long> parVars) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        } else if (expr instanceof Ident) {
            Ident ident = (Ident) expr;
            if (currVars.containsKey(ident.toString())) {
                return currVars.get(ident.toString());
            } else {
                return parVars.get(ident.toString()); 
            }
        }
        else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return (Long)evaluate(binaryExpr.getLeftExpr(), currVars, parVars) + (Long)evaluate(binaryExpr.getRightExpr(), currVars, parVars);
                case BinaryExpr.BMINUS: return (Long)evaluate(binaryExpr.getLeftExpr(), currVars, parVars) - (Long)evaluate(binaryExpr.getRightExpr(), currVars, parVars);
                case BinaryExpr.MULT: return (Long)evaluate(binaryExpr.getLeftExpr(), currVars, parVars) * (Long)evaluate(binaryExpr.getRightExpr(), currVars, parVars);
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            return - ((Long)evaluate(unaryExpr.getExpr(), currVars, parVars));
        } else if (expr instanceof CallExpr) {
                CallExpr call = (CallExpr) expr;
                Ident funcCalled = call.getCallTo();
                List params = new ArrayList<>();
                while (call != null && call.getNeExprList().getExpr() != null) {
                    Long val = evaluate(call.getNeExprList().getExpr());
                    params.add(c)
                }
                runFunc(funcs.get(funcCalled), )
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
