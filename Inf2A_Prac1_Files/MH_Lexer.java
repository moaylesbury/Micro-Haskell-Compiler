
// File:   MH_Lexer.java

// Java template file for lexer component of Informatics 2A Assignment 1.
// Concerns lexical classes and lexer for the language MH (`Micro-Haskell').


import java.io.* ;

class MH_Lexer extends GenLexer implements LEX_TOKEN_STREAM {

static class VarAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "VAR";}
	public int numberOfStates() {return 6;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (CharTypes.isSmall(c)) return 1 ; else return 2;
		case 1: if (CharTypes.isSmall(c) || CharTypes.isLarge(c) || CharTypes.isDigit(c) || c == '\'') return 1 ; else return 2;
		default: return 2;
		}
	}
	
	boolean accepting (int state) {return (state == 1);}
	int dead() {return 3;}
	
}

static class NumAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "NUM";}
	public int numberOfStates() {return 2;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (CharTypes.isDigit(c)) return 1 ; else return 0;
		default: return 0;
		}
	}
	
	boolean accepting (int state) {return (state == 1);}
	int dead() {return 0;}
}

static class BooleanAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "BOOL";}
	public int numberOfStates() {return 9;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (c == 'T') return 1 ; else if (c == 'F') return 5 ; else return 8;
		case 1: if (c == 'r') return 2 ; else return 8;
		case 2: if (c == 'u') return 3 ; else return 8;
		case 3: if (c == 'e') return 4 ; else return 8;
		case 5: if (c == 'a') return 6 ; else return 8;
		case 6: if (c == 'l') return 7 ; else return 8;
		case 7: if (c == 's') return 3 ; else return 8;
		default: return 8;
		}
	}
	
	boolean accepting (int state) {return (state == 4);}
	int dead() {return 8;}
}

static class SymAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "SYM";}
	public int numberOfStates() {return 5;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (CharTypes.isSymbolic(c)) return 1 ; else return 2;
		case 1: if (CharTypes.isSymbolic(c)) return 1 ; else return 1;
		default: return 2;
		}
	}
	
	boolean accepting (int state) {return (state == 1);}
	int dead() {return 2;}
}

static class WhitespaceAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "";}
	public int numberOfStates() {return 5;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (CharTypes.isWhitespace(c)) return 1 ; else return 2;
		case 1: if (CharTypes.isWhitespace(c)) return 1 ; else return 1;
		default: return 2;
		}
	}
	
	boolean accepting (int state) {return (state == 1);}
	int dead() {return 2;}
}

static class CommentAcceptor extends Acceptor implements DFA {
	public String lexClass() {return "";}
	public int numberOfStates() {return 5;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: if (c == '-') return 1 ; else return 4;
		case 1: if (c == '-') return 2 ; else return 4;
		case 2: if (!CharTypes.isNewline(c) && !CharTypes.isSymbolic(c)) return 3 ; else if (c =='-') return 2; else return 2;
		case 3: if (!CharTypes.isNewline(c)) return 3 ; else return 3;
		default: return 4;
		}
	}
	
	boolean accepting (int state) {return (state == 2 || state == 3);}
	int dead() {return 4;}
}

static class TokAcceptor extends Acceptor implements DFA {
	// Illegal state 9 in acceptor for Integer
    String tok ;
    int tokLen ;
    TokAcceptor (String tok) {this.tok = tok ; tokLen = tok.length() ;}
    
    public String lexClass() {return tok;}
	public int numberOfStates() {return tokLen + 2;}
	
	int next (int state, char c) {
		switch (state) {
		
		case 0: 
			if (c == tok.charAt(0)) return 1; else return tokLen + 1;
		
		
		case 1: 
			if (c == tok.charAt(1)) return 2; else return tokLen + 1;
		
		
		case 2: 
		if (tok.length() >= 3) {
			if (c == tok.charAt(2)) return 3;
		} else return tokLen + 1;
		
		
		case 3: 
		if (tok.length() >= 4) {
			if (c == tok.charAt(3)) return 4;
		} else return tokLen + 1;
		
		
		case 4: 
		if (tok.length() >= 5) {
			if (c == tok.charAt(4)) return 5;
		} else return tokLen + 1;
		
		
		case 5: 
		if (tok.length() >= 6) {
			if (c == tok.charAt(5)) return 6;
		} else return tokLen + 1;
		
		
		case 6: 
		if (tok.length() >= 7) {
			if (c == tok.charAt(6)) return 7;
		} else return tokLen + 1;
		
		
		case 7: 
		if (tok.length() >= 8) {
			if (c == tok.charAt(7)) return 8;
		} else return tokLen + 1;
		
		
		default: return tokLen + 1;
		
		
		}
	}
	
	boolean accepting (int state) {return (state == tokLen);}
	int dead() {return tokLen + 1;}
    
}

    // add definitions of MH_acceptors here
	static DFA varAcc = new VarAcceptor();
	static DFA numAcc = new NumAcceptor();
	static DFA boolAcc = new BooleanAcceptor();
	static DFA symAcc = new SymAcceptor();
	static DFA whiteAcc = new WhitespaceAcceptor();
	static DFA commAcc = new CommentAcceptor();
	
	static DFA intAcc = new TokAcceptor("Integer");
	static DFA bAcc = new TokAcceptor("Bool");
	static DFA ifAcc = new TokAcceptor("if");
	static DFA thenAcc = new TokAcceptor("then");
	static DFA elseAcc = new TokAcceptor("else");
	static DFA lbraAcc = new TokAcceptor("(");
	static DFA rbraAcc = new TokAcceptor(")");
	static DFA scAcc = new TokAcceptor(";");
	
	//These should be put in order of importance
	static DFA[] MH_acceptors = new DFA[] {commAcc, symAcc, intAcc, ifAcc, bAcc, thenAcc, elseAcc, lbraAcc, rbraAcc, scAcc, boolAcc, numAcc, varAcc, whiteAcc};

    MH_Lexer (Reader reader) {
	super(reader, MH_acceptors) ;
    }

}











/*
 * int next (int state, char c) {
		System.out.println(state+ " " + c + " " + tok);
		switch (state) {
		
		case 0: if (c == tok.charAt(0)) return 1; else return tokLen + 1;
		case 1: if (c == tok.charAt(1)) return 2; else return tokLen + 1;
		case 2: if (c == tok.charAt(2)) return 3; else return tokLen + 1;
		case 3: if (tokLen >= 3) {
			if (c == tok.charAt(3)) return 4;
		} else return tokLen + 1;
		case 4: if (c == tok.charAt(4)) return 5; else return tokLen + 1;
		case 5: if (c == tok.charAt(5)) return 6; else return tokLen + 1;
		case 6: if (c == tok.charAt(6)) return 7; else return tokLen + 1;
		case 7: if (c == tok.charAt(7)) return 8; else return tokLen + 1;
		default: return tokLen + 1;
		
		}
	}
	
 */




/*case 0: if (tokLen >= 0) {
			if (c == tok.charAt(0)) return 1;
		} else return tokLen + 1;
		
		case 1: if (tokLen >= 1) {
			if (c == tok.charAt(1)) return 2;
		} else return tokLen + 1;
		
		case 2: if (tokLen >= 2) {
			if (c == tok.charAt(2)) return 3;
		} else return tokLen + 1;
		
		case 3: if (tokLen >= 3) {
			if (c == tok.charAt(3)) return 4;
		} else return tokLen + 1;
		
		case 4: if (tokLen >= 4) {
			if (c == tok.charAt(4)) return 5;
		} else return tokLen + 1;
		
		case 5: if (tokLen >= 5) {
			if (c == tok.charAt(5)) return 6;
		} else return tokLen + 1;
		
		case 6: if (tokLen >= 6) {
			if (c == tok.charAt(6)) return 7;
		} else return tokLen + 1;
		
		case 7: if (tokLen >= 7) {
			if (c == tok.charAt(7)) return 8;
		} else return tokLen + 1;
		
		default: return tokLen + 1;
		
		}
		*/




/*case 0: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(0)); if (c == tok.charAt(0)) return 1; else return tokLen + 1;
		case 1: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(1)); if (c == tok.charAt(1)) return 2; else return tokLen + 1;
		case 2: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(2)); if (c == tok.charAt(2)) return 3; else return tokLen + 1;
		case 3: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(3)); if (c == tok.charAt(3)) return 4; else return tokLen + 1;
		case 4: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(4)); if (c == tok.charAt(4)) return 5; else return tokLen + 1;
		case 5: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(5)); if (c == tok.charAt(5)) return 6; else return tokLen + 1;
		case 6: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(6)); if (c == tok.charAt(6)) return 7; else return tokLen + 1;
		case 7: System.out.println(state+ " " + c + " " + tok + " " + tok.charAt(7)); if (c == tok.charAt(7)) return 8; else return tokLen + 1;
		*/
