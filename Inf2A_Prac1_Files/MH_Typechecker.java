
// File:   MH_Typechecker.java

// Java template file for typechecker component of Informatics 2A Assignment 1.
// Provides infrastructure for Micro-Haskell typechecking:
// the core typechecking operation is to be implemented by students.


import java.util.* ;
import java.io.* ;

class MH_Typechecker {

    static MH_Parser MH_Parser1 = MH_Type_Impl.MH_Parser1 ;

    // The core of the typechecker:
    // Computing the MH_TYPE of a given MH_EXP in a given TYPE_ENV.
    // Should raise TypeError if MH_EXP isn't well-typed

    static MH_TYPE IntegerType = MH_Type_Impl.IntegerType ;
    static MH_TYPE BoolType = MH_Type_Impl.BoolType ;

    static MH_TYPE computeType (MH_EXP exp, TYPE_ENV env) 
	throws TypeError, UnknownVariable {
    	
    	if (exp.isVAR()) {
    	 			
    		if (env.typeOf(exp.value()).equals(IntegerType)) {
    			
    			return MH_Type_Impl.IntegerType;
    			
    		} else if (env.typeOf(exp.value()).equals(BoolType)) {
    			
    			return MH_Type_Impl.BoolType;
    			
    		} else {
    			
    			// arrow type
    			return env.typeOf(exp.value());
    			
    		}
    		
    	} else if (exp.isNUM()) {
    		
    		return MH_Type_Impl.IntegerType;
    		
    	} else if (exp.isBOOLEAN()) {
    		
    		return MH_Type_Impl.BoolType;
    		
    	} else if (exp.isAPP()) { 
    		
    		// e1 expected type needs to match e2 type
    		// this is going to be the named function, so will return an arrow type
    		// the expected argument will be the LHS of the arrow 
    		// this expected argument must match e2 type
    		
    		MH_TYPE first = computeType(exp.first(), env); // arrow type ( type -> type )
    		
    		// as dictated by the arrow type for the function, the return type should be the RHS of the arrow 
    		
    		return first.left().equals(computeType(exp.second(), env)) ? first.right() : error("Expected type does not match actual type"); 
    		
    	} else if (exp.isINFIX()) {
    		// infix operations are only performed on integers in this language
    		// thus must check e1 and e2 are IntegerTypes
    		
    		if (computeType(exp.first(), env) == MH_Type_Impl.IntegerType && computeType(exp.second(), env) == MH_Type_Impl.IntegerType) {
    			
    			// if they are comparison operators the type will be BoolType
    			
    			if (exp.infixOp() == "==" || exp.infixOp() == "<=") {
    				
    				return MH_Type_Impl.BoolType;
    				
    			} else { 
    				
    				// otherwise the operator will be + or -, resulting in an IntegerType
    				return MH_Type_Impl.IntegerType;
    				
    			}
    			
    		} else {
    			
    			error("Infix operators act on integer types");
    			return null;
    			
    		}
    		
    	} else if (exp.isIF()) {
    		
    		// if statements of form if e1 then e2 else e3 
    		// where e1 is a condition (BOOLEAN) and the type of e2 is the same as that of e3
    		// thus the return type is that of e2 or e3
    		
    		MH_TYPE returnType = computeType(exp.second(), env);
    		return (computeType(exp.first(), env).equals(BoolType) && computeType(exp.third(), env).equals(returnType)) ? returnType : error("For 'if e1 then e2 else e3' e1 must be a bool, and e2 type must equal e2 type");
    		
    	} else {
    		
    		error("Unknown type");
    		return null;
    				
    	}
    	
    }
    
    
    //Auxillary function allowing errors to be thrown from 'return (cond) ? a : b' statements
    static MH_TYPE error(String msg) throws TypeError {
    	throw new TypeError(msg);
    }


    // Type environments:

    interface TYPE_ENV {
	MH_TYPE typeOf (String var) throws UnknownVariable ;
    }

    static class MH_Type_Env implements TYPE_ENV {

	TreeMap<String,MH_TYPE> env ;

	public MH_TYPE typeOf (String var) throws UnknownVariable {
	    MH_TYPE t = (MH_TYPE)(env.get(var)) ;
	    if (t == null) throw new UnknownVariable(var) ;
	    else return t ;
	}

	// Constructor for cloning a type env
	MH_Type_Env (MH_Type_Env given) {
            this.env = new TreeMap<String,MH_TYPE>() ;
            this.env.putAll(given.env) ;
            // Old version (causes unchecked typecast warning):
	    // this.env = (TreeMap<String,MH_TYPE>)given.env.clone() ;
	}

	// Constructor for building a type env from the type decls 
	// appearing in a program
	MH_Type_Env (TREE prog) throws DuplicatedVariable {
	    this.env = new TreeMap<String,MH_TYPE>() ;
	    TREE prog1 = prog ;
	    while (prog1.getRhs() != MH_Parser.epsilon) {
		TREE typeDecl = prog1.getChildren()[0].getChildren()[0] ;
		String var = typeDecl.getChildren()[0].getValue() ;
		MH_TYPE theType = MH_Type_Impl.convertType 
		    (typeDecl.getChildren()[2]);
		if (env.containsKey(var)) 
		    throw new DuplicatedVariable(var) ;
		else env.put(var,theType) ;
		prog1 = prog1.getChildren()[1] ;
	    }
	    System.out.println ("Type conversions successful.") ;
	}

	// Augmenting a type env with a list of function arguments.
	// Takes the type of the function, returns the result type.
	MH_TYPE addArgBindings (TREE args, MH_TYPE theType) 
	    throws DuplicatedVariable, TypeError {
	    TREE args1=args ;
	    MH_TYPE theType1 = theType ;
	    while (args1.getRhs() != MH_Parser.epsilon) {
		if (theType1.isFun()) {
		    String var = args1.getChildren()[0].getValue() ;
		    if (env.containsKey(var)) {
			throw new DuplicatedVariable(var) ;
		    } else {
			this.env.put(var, theType1.left()) ;
			theType1 = theType1.right() ;
			args1 = args1.getChildren()[1] ;
		    }
		} else throw new TypeError ("Too many function arguments");
	    } ;
	    return theType1 ;
	}
    }

    static MH_Type_Env compileTypeEnv (TREE prog) 
	throws DuplicatedVariable{
	return new MH_Type_Env (prog) ;
    }

    // Building a closure (using lambda) from argument list and body
    static MH_EXP buildClosure (TREE args, MH_EXP exp) {
	if (args.getRhs() == MH_Parser.epsilon) 
	    return exp ;
	else {
	    MH_EXP exp1 = buildClosure (args.getChildren()[1], exp) ;
	    String var = args.getChildren()[0].getValue() ;
	    return new MH_Exp_Impl (var, exp1) ;
	}
    }

    // Name-closure pairs (result of processing a TermDecl).
    static class Named_MH_EXP {
	String name ; MH_EXP exp ;
	Named_MH_EXP (String name, MH_EXP exp) {
	    this.name = name; this.exp = exp ;
	}
    }

    static Named_MH_EXP typecheckDecl (TREE decl, MH_Type_Env env) 
	throws TypeError, UnknownVariable, DuplicatedVariable,
	       NameMismatchError {
    // typechecks the given decl against the env, 
    // and returns a name-closure pair for the entity declared.
	String theVar = decl.getChildren()[0].getChildren()[0].getValue();
	String theVar1= decl.getChildren()[1].getChildren()[0].getValue();
	if (!theVar.equals(theVar1)) 
	    throw new NameMismatchError(theVar,theVar1) ; 
	MH_TYPE theType = 
	    MH_Type_Impl.convertType (decl.getChildren()[0].getChildren()[2]) ;
	MH_EXP theExp =
	    MH_Exp_Impl.convertExp (decl.getChildren()[1].getChildren()[3]) ;
	TREE theArgs = decl.getChildren()[1].getChildren()[1] ;
	MH_Type_Env theEnv = new MH_Type_Env (env) ;
	MH_TYPE resultType = theEnv.addArgBindings (theArgs, theType) ;
	MH_TYPE expType = computeType (theExp, theEnv) ;
	if (expType.equals(resultType)) {
	    return new Named_MH_EXP (theVar,buildClosure(theArgs,theExp));
	}
	else throw new TypeError ("RHS of declaration of " +
				  theVar + " has wrong type") ;
    }

    static MH_Exp_Env typecheckProg (TREE prog, MH_Type_Env env)
	throws TypeError, UnknownVariable, DuplicatedVariable,
	       NameMismatchError {
	TREE prog1 = prog ;
	TreeMap<String,MH_EXP> treeMap = new TreeMap<String,MH_EXP>() ;
	while (prog1.getRhs() != MH_Parser.epsilon) {
	    TREE theDecl = prog1.getChildren()[0] ;
	    Named_MH_EXP binding = typecheckDecl (theDecl, env) ;
	    treeMap.put (binding.name, binding.exp) ;
	    prog1 = prog1.getChildren()[1] ;
	}
	System.out.println ("Typecheck successful.") ;
	return new MH_Exp_Env (treeMap) ;
    }

    // For testing:

    public static void main (String[] args) throws Exception {
	Reader reader = new BufferedReader (new FileReader (args[0])) ;
	// try {
	    LEX_TOKEN_STREAM MH_Lexer = 
		new CheckedSymbolLexer (new MH_Lexer (reader)) ;
	    TREE prog = MH_Parser1.parseTokenStream (MH_Lexer) ;
	    MH_Type_Env typeEnv = compileTypeEnv (prog) ;
	    MH_Exp_Env runEnv = typecheckProg (prog, typeEnv) ;
	// } catch (Exception x) {
        //  System.out.println ("MH Error: " + x.getMessage()) ;
	// }
    }
}
