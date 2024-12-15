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

    private boolean isEof = false; // End of file flag
    private char nextChar = ' '; // Current character being processed
    private BufferedReader input;

    // Constructor to initialize the file reader
    public TokenStream(String fileName) {
        try {
            input = new BufferedReader(new FileReader(fileName));
            nextChar = readChar(); // Initialize with the first character
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            isEof = true;
        }
    }

    // Returns the next token from the input stream
    public Token nextToken() {
        Token t = new Token();
        t.setType("Other");
        t.setValue("");

        skipWhiteSpace();

        // Handle comments
        if (nextChar == '/') {
            nextChar = readChar();
            if (nextChar == '/') { // Single-line comment
                while (!isEndOfLine(nextChar) && !isEof) {
                    nextChar = readChar();
                }
                skipWhiteSpace();
                return nextToken(); // Skip the comment and fetch the next token
            } else { // '/' as an operator
                t.setValue("/");
                t.setType("Operator");
                return t;
            }
        }

        // Handle multi-character operators
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
                    if (nextChar == '=') { // Handle <=, >=, ==, !=, :=
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                    }
                    return t;

                case '*':
                    nextChar = readChar();
                    if (nextChar == '*') { // Handle **
                        t.setValue(t.getValue() + nextChar);
                        nextChar = readChar();
                        if (nextChar == '*') { // Handle ***
                            t.setValue(t.getValue() + nextChar);
                            nextChar = readChar();
                        }
                    }
                    return t;

                case '&':
                    nextChar = readChar();
                    if (nextChar == '&') { // Handle &&
                        t.setValue("&&");
                        nextChar = readChar();
                    }
                    return t;

                case '|':
                    nextChar = readChar();
                    if (nextChar == '|') { // Handle ||
                        t.setValue("||");
                        nextChar = readChar();
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

        // Handle keywords, identifiers, and literals
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
            return t;
        }

        // Handle integer literals
        if (isDigit(nextChar)) {
            t.setType("Literal");
            while (isDigit(nextChar)) {
                t.setValue(t.getValue() + nextChar);
                nextChar = readChar();
            }
            return t;
        }

        // Handle unknown characters
        if (!isWhiteSpace(nextChar) && !isEof) {
            t.setType("Other");
            t.setValue(String.valueOf(nextChar));
            nextChar = readChar();
            return t;
        }

        return t; // Return token
    }

    // Reads the next character from the input stream
    private char readChar() {
        try {
            int i = input.read();
            if (i == -1) {
                isEof = true;
                return (char) 0;
            }
            return (char) i;
        } catch (IOException e) {
            isEof = true;
            return (char) 0;
        }
    }

    // Skip whitespace characters
    private void skipWhiteSpace() {
        while (isWhiteSpace(nextChar)) {
            nextChar = readChar();
        }
    }

    // Checks if a string is a keyword
    private boolean isKeyword(String s) {
        return s.equals("main") || s.equals("if") || s.equals("else") ||
               s.equals("while") || s.equals("bool") || s.equals("integer");
    }

    // Checks if a character is whitespace
    private boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }

    // Checks if a character is the end of a line
    private boolean isEndOfLine(char c) {
        return c == '\r' || c == '\n';
    }

    // Checks if a character is a separator
    private boolean isSeparator(char c) {
        return c == '(' || c == ')' || c == '{' || c == '}' ||
               c == ';' || c == ',' || c == ':' || c == '[' || c == ']';
    }

    // Checks if a character is an operator
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' ||
               c == '<' || c == '>' || c == '=' || c == '!' ||
               c == ':' || c == '&' || c == '|';
    }

    // Checks if a character is a letter
    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    // Checks if a character is a digit
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // Checks if the end of the file has been reached
    public boolean isEndofFile() {
        return isEof;
    }
}
