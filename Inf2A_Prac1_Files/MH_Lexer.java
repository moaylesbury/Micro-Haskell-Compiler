
// File:   MH_Lexer.java

// Java template file for lexer component of Informatics 2A Assignment 1.
// Concerns lexical classes and lexer for the language MH (`Micro-Haskell').


import java.io.* ;

class MH_Lexer extends GenLexer implements LEX_TOKEN_STREAM {

static class VarAcceptor extends Acceptor implements DFA {
    // add code here
}

static class NumAcceptor extends Acceptor implements DFA {
    // add code here
}

static class BooleanAcceptor extends Acceptor implements DFA {
    // add code here
}

static class SymAcceptor extends Acceptor implements DFA {
    // add code here
}

static class WhitespaceAcceptor extends Acceptor implements DFA {
    // add code here
}

static class CommentAcceptor extends Acceptor implements DFA {
    // add code here
}

static class TokAcceptor extends Acceptor implements DFA {

    String tok ;
    int tokLen ;
    TokAcceptor (String tok) {this.tok = tok ; tokLen = tok.length() ;}
    
    // add code here
}

    // add definitions of MH_acceptors here

    MH_Lexer (Reader reader) {
	super(reader,MH_acceptors) ;
    }

}

