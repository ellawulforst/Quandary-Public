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
        List<Object> quandaryArgs = new ArrayList<Object>();
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
        FuncDef currentFunc;// = funcList.getFuncDef();
        while (funcList != null) {
            currentFunc = funcList.getFuncDef();
            if (currentFunc.getFuncName().equals("main")) {
                mainFunc = currentFunc;
            }
            funcs.put(currentFunc.getFuncName(), currentFunc);
            funcList = funcList.getFuncDefList();
        }
        return runFunc(mainFunc, args);
    }

    

    Object runFunc(FuncDef fd, List args) {
            //System.out.println("runfunc for: " + fd);
            Map<String, Object> scopeVars = new HashMap<String, Object>();
            Map<String, Object> cmdArgs = new HashMap<String, Object>();
            
            //if there are no params, fd has an empty decl list, dont need to do anything
            //if multiple params, need to loop through decl list in the func def and add each
            FormalDeclList declList = fd.getFormalDeclList();
            VarDecl var;
            int i = 0;
            while (declList != null) {
                var = declList.getVarDecl();

                cmdArgs.put(var.getIdent(), (args.toArray())[i++]);
                
                declList = declList.getNeFormalDeclList();
            }
            
            StmtList stmtList = fd.getStmtList();
            
            while (stmtList != null) {
                Object ret = evaluate(stmtList.getStmt(), scopeVars, cmdArgs);
                
                if (ret instanceof VarAssign) {
                    //System.out.println("entered assign in runfunc");
                    VarAssign varAssign = (VarAssign) ret;
                    String ident = varAssign.getVarDecl().getIdent();
                    Object value = evaluate(varAssign.getExpr(), scopeVars, cmdArgs);
                    if(scopeVars.containsKey(ident)) {
                        fatalError("wrong", 222);
                    } else {
                        scopeVars.put(ident, value);
                    }
                } else if (ret instanceof Return) {
                    //System.out.println("entered return in runfunc");
                    Return ret2 = (Return) ret;
                    //fatalError("am in return of runfunc. ret expr: " + ret2, 0);
                    return evaluate(ret2.getExpr(), scopeVars, cmdArgs);
                    
                } else if (ret instanceof Reassign) {
                    //System.out.println("entered reassign in runfunc");
                    Reassign reassign = (Reassign) ret;
                    String ident = reassign.getIdent();
                    Object value = evaluate(reassign.getExpr(), scopeVars, cmdArgs);
                    scopeVars.replace(ident, value);
                }
                    stmtList = stmtList.getStmtList();
            }
            return null;
    }

    Object evaluate(Stmt stmt, Map<String, Object> currVars, Map<String, Object> parVars) {
        //System.out.println("stmt being evaluated: " + stmt);
        //System.out.println("currevars at start of eval stmt: " + currVars);
        //System.out.println("parvars at start of eval stmt: " + parVars);
        //will either return a return or a variable declaration
        if (stmt instanceof VarAssign || stmt instanceof Return || stmt instanceof Reassign) {
            //System.out.println("stmt is varassign, return, or reassign");
            // System.out.println("class: " + stmt.getClass());
            // System.out.println("currvars: " + currVars);
            // System.out.println("parvars: " + parVars);
            return stmt;
        }   else if (stmt instanceof StmtList) {
                //System.out.println("entered stmtlist in eval(stmt)");
                StmtList stmtList = (StmtList) stmt;
                Map<String, Object> newParVars = new HashMap<String, Object>(parVars);
                newParVars.putAll(currVars);
                Map<String, Object> subVars = new HashMap<String, Object>();
                while (stmtList != null) {
                    //System.out.println("Entered while loop in evaluate stmlist");
                    //System.out.println("line 190 stmt: " + stmt);
                    Object ret = evaluate(stmtList.getStmt(), subVars, newParVars);
                    if (ret instanceof VarAssign) {
                        //System.out.println("in varassign");
                        VarAssign varAssign = (VarAssign) ret;
                        String ident = varAssign.getVarDecl().getIdent();
                        Object value = evaluate(varAssign.getExpr(), subVars, newParVars);
                        if(subVars.containsKey(ident)) {
                            fatalError("wrong", 222);
                        }
                        if(newParVars.containsKey(ident)) {
                            fatalError("wrong", 222);
                        } else {
                            subVars.put(ident, value);
                        }
                    } else if (ret instanceof Return) {
                        //System.out.println("in return");
                        Return ret2 = (Return) ret;
                        //was making a const expr here but return can return a rf too
                        //just returning what the expr evaluates to in general
                        //System.out.println("return getExpr: " + ret2.getExpr());
                        
                        return evaluate(ret2.getExpr(), subVars, newParVars);
                    } else if (ret instanceof Reassign) {
                        //System.out.println("in reassign");
                        Reassign reassign = (Reassign) ret;
                        String ident = reassign.getIdent();
                        Object value = evaluate(reassign.getExpr(), subVars, newParVars);
                        //System.out.println("value to reassign: " + value);
                        if (subVars.containsKey(ident)) {
                            subVars.replace(ident, value);
                        }
                        if (newParVars.containsKey(ident)) {
                            newParVars.replace(ident, value);
                        }
                        if (currVars.containsKey(ident)) {
                            currVars.replace(ident, value);
                        }
                        if (parVars.containsKey(ident)) {
                            parVars.replace(ident, value);
                        }
                        // System.out.println("in else, ret 216: " + ret);
                        // System.out.println("in else, class on 216: " + ret.getClass());
                        // System.out.println("currvars: " + currVars);
                        // System.out.println("parvars: " + parVars);
                        // System.out.println("subvars: " + subVars);
                        // System.out.println("newparvars: " + newParVars);
                    }
                    
                    stmtList = stmtList.getStmtList();
                }
                //System.out.println("at line 239");
                return stmt;
        } else if (stmt instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) stmt;
                Boolean cond = evaluate(ifStmt.getCond(), currVars, parVars);
                if (cond) {
                    //fatalError("yo what the fuck",21);
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
        } else if (stmt instanceof While) {
                While whileStmt = (While) stmt;
                while (evaluate(whileStmt.getCond(), currVars, parVars)) {
                    
                    evaluate(whileStmt.getStmt(), currVars, parVars);
                    //System.out.println("currevars at end of while: " + currVars);
                    //System.out.println("parvars at end of while: " + parVars);
                }
        } else if (stmt instanceof CallStmt) {
            //GET EXPR FORM CALL STMT, RUN FUNC ON IT, SEND IDENT AND EXPR TO CALLEXPR
                //System.out.println("in callstmt");
                CallStmt callStmt = (CallStmt) stmt;
                //get funcdef and exprlist from callstmt
                String callIdent = callStmt.getCallTo();
                ExprList params = callStmt.getParams();

                CallExpr callExpr = new CallExpr(callIdent, params, null);

                evaluate(callExpr, currVars, parVars);
        }  else if (stmt instanceof Print) {
                Print print = (Print) stmt;
                System.out.println(evaluate(print.getExpr(), currVars, parVars));
        }
            //System.out.println("literally nothing hapened");
            return null;
        
    }

    Boolean evaluate(CondExpr condExpr, Map<String, Object> currVars, Map<String, Object> parVars) {
            switch (condExpr.getOperator()) {
                case CondExpr.LE: return (boolean)(((long) evaluate(condExpr.getLeftExpr(), currVars, parVars)) <= ((long)evaluate(condExpr.getRightExpr(), currVars, parVars)));
                case CondExpr.GE: return (boolean)((long) evaluate(condExpr.getLeftExpr(), currVars, parVars) >= (long)evaluate(condExpr.getRightExpr(), currVars, parVars));
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

    Object evaluate(Expr expr, Map<String, Object> currVars, Map<String, Object> parVars) {
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
                case BinaryExpr.DOT: return new Ref(evaluate(binaryExpr.getLeftExpr(), currVars, parVars), evaluate(binaryExpr.getRightExpr(), currVars, parVars), false);
                default: throw new RuntimeException("Unhandled operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            return - ((Long)evaluate(unaryExpr.getExpr(), currVars, parVars));
        } else if (expr instanceof CallExpr) {
                //System.out.println("evaluting call expr");
                CallExpr call = (CallExpr) expr;
                //System.out.println("call: " + call);
                //get funcdef and exprlist from callexpr
                //search funclist for funcdef for the ident
                
                FuncDef funcDef = funcs.get(call.getCallTo());
                //System.out.println("callto: " + call.getCallTo());
                //System.out.println("funcdef: " + funcDef);

                //loop thruogh exprlist, evaluate each expr, add to para map
                List params = new ArrayList<>();
                ExprList eList = call.getParams();
                //System.out.println("elist: " + eList);
                while (eList != null) {
                    params.add(evaluate(eList.getExpr(), currVars, parVars));
                    eList = eList.getNeExprList();
                }
                switch (call.getCallTo()) {
                    case "randomInt":
                        return ThreadLocalRandom.current().nextLong(0, (long) params.get(0));
                    case "left" :
                        Ref leftRef = (Ref) params.get(0);
                        return leftRef.getLeft();
                    case "right" :
                        //System.out.println("in call to right");
                        //System.out.println("params: " + params);
                        //System.out.println("currvars: " + currVars);
                        //System.out.println("parVars: " + parVars);
                        Ref rightRef = (Ref) params.get(0);
                        return rightRef.getRight();
                    case "isAtom" :
                        Object isAtomObject = params.get(0);
                        //System.out.println(isAtomObject);
                    case "isNil" :
                        Ref isNilRef = (Ref) params.get(0);
                        //System.out.println("isnilref: " + isNilRef);
                        //System.out.println("isnil: " + isNilRef.isNil());
                        if (isNilRef == null) {
                            return 1;
                        }
                        return isNilRef.isNil();
                    case "setLeft" :
                        Ref setLeftRef =  (Ref) params.get(0);
                        setLeftRef.setLeft(params.get(1));
                        break;
                    case "setRight" :
                        // System.out.println("in setright");
                        // System.out.println("ref object we're chanigng " + params.get(0));
                        // System.out.println("changing right to: " + params.get(1));
                        Ref setRightRef = (Ref) params.get(0);
                        setRightRef.setRight(params.get(1));
                        break;
                    default:
                        //System.out.println("in default");
                        return runFunc(funcs.get(call.getCallTo()), params);
                }   
                 return 222;       
                
        }  else if (expr instanceof Cast) {
            Cast cast = (Cast) expr;
            //TODO: ADD PRECEDENCE RULES
            return evaluate(cast.getCastedExpr(), currVars, parVars);
            
        } else if (expr == null) {
            return new Ref(null, null, true);
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
