// TokenStream.java

// Implementation of the Scanner for JAY

// This code DOES NOT implement a scanner for JAY yet. You have to complete
// the code and also make sure it implements a scanner for JAY - not something
// else.

	
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenStream {

    private boolean isEof = false;
    private char nextChar = ' ';
    private BufferedReader input;

    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            isEof = true;
        }
    }

    public Token nextToken() {
        Token t = new Token();
        t.setType("Other");
        t.setValue("");

        skipWhiteSpace();

        // Handle comments
        while (nextChar == '/') {
            nextChar = readChar();
            if (nextChar == '/') {
                while (!isEndOfLine(nextChar) && !isEof) {
                    nextChar = readChar();
                }
                skipWhiteSpace();
                continue;
            } else {
                t.setValue("/");
                t.setType("Operator");
                return t;
            }
        }

        // Handle operators, including multi-character ones
        if (isOperator(nextChar)) {
            t.setType("Operator");
            t.setValue(t.getValue() + nextChar);
            switch (nextChar) {
                case '<':
                case '>':
                case '=':
                case '!':
                case ':':
                    nextChar = readChar();
                    if (nextChar == '=') {
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                    }
                    return t;

                case '*':
                    nextChar = readChar();
                    if (nextChar == '*') {
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                        if (nextChar == '*') {
                            t.setValue(t.getValue() + nextChar);
                            nextChar = readChar();
                        }
                    }
                    return t;

                case '|':
                    nextChar = readChar();
                    if (nextChar == '|') {
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                        return t;
                    } else {
                        t.setType("Other");
                    }
                    return t;

                case '&':
                    nextChar = readChar();
                    if (nextChar == '&') {
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                        return t;
                    } else {
                        t.setType("Other");
                    }
                    return t;

                default:
                    nextChar = readChar();
                    return t;
            }
        }

        // Handle separators
        if (isSeparator(nextChar)) {
            t.setType("Separator");
            t.setValue(String.valueOf(nextChar));
            nextChar = readChar();
            return t;
        }

        // Handle identifiers, keywords, and literals
        if (isLetter(nextChar)) {
            t.setType("Identifier");
            while (isLetter(nextChar) || isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            if (isKeyword(t.getValue())) {
                t.setType("Keyword");
            } else if (t.getValue().equals("True") || t.getValue().equals("False")) {
                t.setType("Literal");
            }
            if (isEndOfToken(nextChar)) {
                return t;
            }
        }

        // Handle integer literals
        if (isDigit(nextChar)) {
            t.setType("Literal");
            while (isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            if (isEndOfToken(nextChar)) {
                return t;
            }
        }

        t.setType("Other");

        if (isEof) {
            return t;
        }

        // Handle unknown tokens
        while (!isEndOfToken(nextChar)) {
            t.setValue(t.getValue() + nextChar);
            nextChar = readChar();
        }

        skipWhiteSpace();

        return t;
    }

    private char readChar() {
        int i = 0;
        if (isEof) return (char) 0;
        try {
            i = input.read();
        } catch (IOException e) {
            System.exit(-1);
        }
        if (i == -1) {
            isEof = true;
            return (char) 0;
        }
        return (char) i;
    }

    private boolean isKeyword(String s) {
        return s.equals("main") || s.equals("integer") || s.equals("bool") ||
               s.equals("if") || s.equals("else") || s.equals("while");
    }

    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f';
    }

    private boolean isEndOfLine(char c) {
        return c == '\r' || c == '\n' || c == '\f';
    }

    private boolean isEndOfToken(char c) {
        return isWhiteSpace(nextChar) || isOperator(nextChar) || isSeparator(nextChar) || isEof;
    }

    private void skipWhiteSpace() {
        while (!isEof && isWhiteSpace(nextChar)) {
            nextChar = readChar();
        }
    }

    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' || 
               c == ';' || c == ',' || c == ':';
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' ||
               c == '<' || c == '>' || c == '=' || c == '!' ||
               c == '&' || c == '|' || c == ':';
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    public boolean isEndofFile() {
        return isEof;
    }
}
