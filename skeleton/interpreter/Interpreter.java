package interpreter;

import java.io.*;
import java.util.Random;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    Map<String, FuncDef> funcs = new HashMap<String, FuncDef>();

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
        List quandaryArgs = new ArrayList<Long>();
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
            filename = args[i++];
            while(i < args.length) {
                quandaryArgs.add(Long.valueOf(args[i]));
                i++;
            }
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
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArgs).toString();
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

    Object executeRoot(Program astRoot, List args) {
        FuncDef mainFunc = null;
        FuncDefList funcList = astRoot.getFuncDefList();
        //System.out.println("astRoot.getFuncDefList(): " + funcList);
        FuncDef currentFunc;// = funcList.getFuncDef();
        while (funcList != null) {
            currentFunc = funcList.getFuncDef();
            //System.out.println("curr func: " + currentFunc);
            if (currentFunc.getFuncName().equals("main")) {
                mainFunc = currentFunc;
            }
            funcs.put(currentFunc.getFuncName(), currentFunc);
            //System.out.println("funclist: " + funcList);
            funcList = funcList.getFuncDefList();
            //System.out.println("funclist: " + funcList);
        }
        //System.out.println("mainfunc line 127: " + mainFunc);
        //System.out.println("args line 127: " + args);
        return runFunc(mainFunc, args);
    }

    Object runFunc(FuncDef fd, List args) {
            
            System.out.println("not randomInt");
            //System.out.println("hi im here");
            //System.out.println("fd: " + fd);
            Map<String, Long> scopeVars = new HashMap<String, Long>();
            Map<String, Long> cmdArgs = new HashMap<String, Long>();
            
            //if there are no params, fd has an empty decl list, dont need to do anything
            //if multiple params, need to loop through decl list in the func def and add each
            //System.out.println("fd: " + fd);
            FormalDeclList declList = fd.getFormalDeclList();
            VarDecl var;
            int i = 0;
            while (declList != null) {
                //System.out.println("declList: " + declList);
                //System.out.println("declList: " + declList);
                //System.out.println("declList.getNeFormalDeclList(): " + declList.getVarDecl().getIdent());
                //System.out.println("value: " + args.toArray()[0]);

                var = declList.getVarDecl();
                //System.out.println("var: " + var);
                //System.out.println("value: " + args.toArray()[i]);
                cmdArgs.put(var.getIdent(), (Long) (args.toArray())[i++]);

                declList = declList.getNeFormalDeclList();
                //System.out.println("declList: " + declList);
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
                System.out.println("evaluting call expr");
                CallExpr call = (CallExpr) expr;
                //get funcdef and exprlist from callexpr
                //search funclist for funcdef for the ident
                
                FuncDef funcDef = funcs.get(call.getCallTo());
                System.out.println("fd: " + funcDef);

                //loop thruogh exprlist, evaluate each expr, add to para map
                List params = new ArrayList<>();
                ExprList eList = call.getParams();
                while (eList != null) {
                    params.add(evaluate(eList.getExpr(), currVars, parVars));
                    eList = eList.getNeExprList();
                }

                if (call.getCallTo().equals("randomInt")) {
                    System.out.println("in random int");
                    return ThreadLocalRandom.current().nextLong(0, (long) params.get(0));
                }
                
                //FormalDeclList varList = 
                String funcCalled = call.getCallTo();
                //if is randomInt
                if (funcCalled.equals("randomInt")) {
                    return new Random().nextLong();
                }
                System.out.println("calling runFunc to " + funcCalled);
                return runFunc(funcs.get(funcCalled), params);
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
