// ConcreteSyntax.java

// Implementation of the Recursive Descent Parser algorithm

//  Each method corresponds to a concrete syntax grammar rule, 
// which appears as a comment at the beginning of the method.

// This code DOES NOT implement a parser for KAY. You have to complete
// the code and also make sure it implements a parser for KAY - not something
// else.

public class ConcreteSyntax {

    public Token token; // current token being analyzed
    public TokenStream input; // stream of tokens from lexical analysis

    public ConcreteSyntax(TokenStream ts) {
        input = ts;
        token = input.nextToken();
    }

    private String SyntaxError(String expected) {
        return "Syntax error - Expecting: " + expected + " But saw: " + token.getType() + ": " + token.getValue();
    }

    private void match(String expected) {
        if (token.getValue().equals(expected)) {
            token = input.nextToken();
        } else {
            throw new RuntimeException(SyntaxError(expected));
        }
    }

    public Program program() {
        // Program --> main '{' Declarations Statements '}'
        Program p = new Program();
        match("main");
        match("{");
        p.decpart = declarations(); // Parse declarations
        p.body = statements();      // Parse statements block
        match("}");
        return p;
    }

    private Declarations declarations() {
        Declarations ds = new Declarations();
        while (token.getValue().equals("integer") || token.getValue().equals("bool")) {
            declaration(ds);
        }
        return ds;
    }

    private void declaration(Declarations ds) {
        Type t = type();
        identifiers(ds, t);
        match(";");
    }

    private Type type() {
        Type t = null;
        if (token.getValue().equals("integer")) {
            t = new Type("integer");
        } else if (token.getValue().equals("bool")) {
            t = new Type("bool");
        } else {
            throw new RuntimeException(SyntaxError("integer | bool"));
        }
        token = input.nextToken();
        return t;
    }

    private void identifiers(Declarations ds, Type t) {
        Declaration d = new Declaration();
        d.t = t;
        if (token.getType().equals("Identifier")) {
            d.v = new Variable();
            d.v.id = token.getValue();
            ds.addElement(d);
            token = input.nextToken();
            while (token.getValue().equals(",")) {
                token = input.nextToken();
                if (token.getType().equals("Identifier")) {
                    d = new Declaration();
                    d.t = t;
                    d.v = new Variable();
                    d.v.id = token.getValue();
                    ds.addElement(d);
                    token = input.nextToken();
                } else {
                    throw new RuntimeException(SyntaxError("Identifier"));
                }
            }
        } else {
            throw new RuntimeException(SyntaxError("Identifier"));
        }
    }

    private Block statements() {
        Block b = new Block();
        while (!token.getValue().equals("}")) {
            b.blockmembers.addElement(statement());
        }
        return b;
    }

    private Statement statement() {
        if (token.getValue().equals(";")) {
            token = input.nextToken();
            return new Skip();
        } else if (token.getValue().equals("{")) {
            token = input.nextToken();
            Block b = statements();
            match("}");
            return b;
        } else if (token.getType().equals("Identifier")) {
            return assignment();
        } else {
            throw new RuntimeException(SyntaxError("Statement"));
        }
    }

    private Assignment assignment() {
        Assignment a = new Assignment();
        if (token.getType().equals("Identifier")) {
            a.target = new Variable();
            a.target.id = token.getValue();
            token = input.nextToken();
            match(":=");
            a.source = expression();
            match(";");
        } else {
            throw new RuntimeException(SyntaxError("Identifier"));
        }
        return a;
    }

    private Expression expression() {
        return factor();
    }

    private Expression factor() {
        Expression e = null;
        if (token.getType().equals("Identifier")) {
            Variable v = new Variable();
            v.id = token.getValue();
            e = v;
            token = input.nextToken();
        } else if (token.getType().equals("Literal")) {
            Value v = null;
            if (isInteger(token.getValue())) {
                v = new Value(Integer.parseInt(token.getValue()));
            } else if (token.getValue().equals("True")) {
                v = new Value(true);
            } else if (token.getValue().equals("False")) {
                v = new Value(false);
            } else {
                throw new RuntimeException(SyntaxError("Literal"));
            }
            e = v;
            token = input.nextToken();
        } else {
            throw new RuntimeException(SyntaxError("Identifier | Literal"));
        }
        return e;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
