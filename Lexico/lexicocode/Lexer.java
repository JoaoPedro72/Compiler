package lexicocode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lexer {
        private final String input;
        private final SymbolTable symbolTable;
        private final List<LexicalError> errors = new ArrayList<>();
        private int index;
        private int line = 1;
        private int column = 1;

        Lexer(String input, SymbolTable symbolTable) {
            this.input = Objects.requireNonNull(input);
            this.symbolTable = Objects.requireNonNull(symbolTable);
        }

        List<LexicalError> getErrors() {
            return errors;
        }

        Token nextToken() {
            for (;;) {
                skipWhitespaceAndComments();

                int startLine = line;
                int startColumn = column;
                char current = currentChar();

                if (current == '\0') {
                    return new Token(TokenType.EOF, "<EOF>", startLine, startColumn);
                }

                if (isAsciiLetter(current)) {
                    return scanWord(startLine, startColumn);
                }

                if (Character.isDigit(current)) {
                    Token number = scanNumber(startLine, startColumn);
                    if (number != null) {
                        return number;
                    }
                    continue;
                }

                if (current == '"') {
                    Token text = scanString(startLine, startColumn);
                    if (text != null) {
                        return text;
                    }
                    continue;
                }

                switch (current) {
                    case ':':
                        advance();
                        if (currentChar() != '=') {
                            addError(startLine, startColumn, "esperado '=' apos ':' para formar ':='");
                            continue;
                        }
                        advance();
                        return new Token(TokenType.ASSIGN, ":=", startLine, startColumn);
                    case '+':
                        advance();
                        return new Token(TokenType.PLUS, "+", startLine, startColumn);
                    case '-':
                        advance();
                        return new Token(TokenType.MINUS, "-", startLine, startColumn);
                    case '*':
                        advance();
                        return new Token(TokenType.TIMES, "*", startLine, startColumn);
                    case '/':
                        advance();
                        return new Token(TokenType.DIVIDE, "/", startLine, startColumn);
                    case '%':
                        advance();
                        return new Token(TokenType.MOD, "%", startLine, startColumn);
                    case '>':
                        advance();
                        if (currentChar() == '=') {
                            advance();
                            return new Token(TokenType.GE, ">=", startLine, startColumn);
                        }
                        return new Token(TokenType.GT, ">", startLine, startColumn);
                    case '<':
                        advance();
                        if (currentChar() == '=') {
                            advance();
                            return new Token(TokenType.LE, "<=", startLine, startColumn);
                        }
                        if (currentChar() == '>') {
                            advance();
                            return new Token(TokenType.NE, "<>", startLine, startColumn);
                        }
                        return new Token(TokenType.LT, "<", startLine, startColumn);
                    case '=':
                        advance();
                        if (currentChar() == '=') {
                            advance();
                            return new Token(TokenType.EQ, "==", startLine, startColumn);
                        }
                        return new Token(TokenType.EQ, "=", startLine, startColumn);
                    case '&':
                        advance();
                        if (currentChar() != '&') {
                            addError(startLine, startColumn, "operador '&' isolado nao e valido; use 'and' ou '&&'");
                            continue;
                        }
                        advance();
                        return new Token(TokenType.AND, "&&", startLine, startColumn);
                    case '|':
                        advance();
                        if (currentChar() != '|') {
                            addError(startLine, startColumn, "operador '|' isolado nao e valido; use 'or' ou '||'");
                            continue;
                        }
                        advance();
                        return new Token(TokenType.OR, "||", startLine, startColumn);
                    case '(':
                        advance();
                        return new Token(TokenType.LPAREN, "(", startLine, startColumn);
                    case ')':
                        advance();
                        return new Token(TokenType.RPAREN, ")", startLine, startColumn);
                    case '{':
                        advance();
                        return new Token(TokenType.LBRACE, "{", startLine, startColumn);
                    case '}':
                        advance();
                        return new Token(TokenType.RBRACE, "}", startLine, startColumn);
                    case ',':
                        advance();
                        return new Token(TokenType.COMMA, ",", startLine, startColumn);
                    case ';':
                        advance();
                        return new Token(TokenType.SEMICOLON, ";", startLine, startColumn);
                    default:
                        addError(startLine, startColumn, "caractere invalido '" + printable(current) + "'");
                        advance();
                }
            }
        }

        private void skipWhitespaceAndComments() {
            boolean consumed;
            do {
                consumed = false;
                while (Character.isWhitespace(currentChar())) {
                    advance();
                    consumed = true;
                }
                if (currentChar() == '/' && peekChar() == '/') {
                    while (currentChar() != '\0' && currentChar() != '\n') {
                        advance();
                    }
                    consumed = true;
                } else if (currentChar() == '/' && peekChar() == '*') {
                    int commentLine = line;
                    int commentColumn = column;
                    advance();
                    advance();
                    while (!(currentChar() == '*' && peekChar() == '/')) {
                        if (currentChar() == '\0') {
                            addError(commentLine, commentColumn, "comentario de bloco nao terminado");
                            return;
                        }
                        advance();
                    }
                    advance();
                    advance();
                    consumed = true;
                }
            } while (consumed);
        }

        private Token scanWord(int startLine, int startColumn) {
            StringBuilder builder = new StringBuilder();
            while (isAsciiLetter(currentChar()) || Character.isDigit(currentChar())) {
                builder.append(currentChar());
                advance();
            }
            String lexeme = builder.toString();
            TokenType reserved = symbolTable.findReserved(lexeme);
            if (reserved != null) {
                return new Token(reserved, lexeme, startLine, startColumn);
            }
            symbolTable.installIdentifier(lexeme, startLine);
            return new Token(TokenType.IDENTIFIER, lexeme, startLine, startColumn);
        }

        private Token scanNumber(int startLine, int startColumn) {
            StringBuilder builder = new StringBuilder();
            while (Character.isDigit(currentChar())) {
                builder.append(currentChar());
                advance();
            }

            if (isAsciiLetter(currentChar())) {
                while (isAsciiLetter(currentChar()) || Character.isDigit(currentChar())) {
                    builder.append(currentChar());
                    advance();
                }
                addError(startLine, startColumn, "identificador invalido iniciado por digito: '" + builder + "'");
                return null;
            }

            if (currentChar() == '.') {
                builder.append('.');
                advance();
                if (!Character.isDigit(currentChar())) {
                    addError(startLine, startColumn, "constante real malformada: '" + builder + "'");
                    while (isAsciiLetter(currentChar()) || Character.isDigit(currentChar())) {
                        builder.append(currentChar());
                        advance();
                    }
                    return null;
                }
                while (Character.isDigit(currentChar())) {
                    builder.append(currentChar());
                    advance();
                }
                if (isAsciiLetter(currentChar())) {
                    while (isAsciiLetter(currentChar()) || Character.isDigit(currentChar())) {
                        builder.append(currentChar());
                        advance();
                    }
                    addError(startLine, startColumn, "constante real malformada: '" + builder + "'");
                    return null;
                }
                return new Token(TokenType.REAL_CONST, builder.toString(), startLine, startColumn);
            }

            return new Token(TokenType.INTEGER_CONST, builder.toString(), startLine, startColumn);
        }

        private Token scanString(int startLine, int startColumn) {
            StringBuilder builder = new StringBuilder();
            builder.append('"');
            advance();
            while (currentChar() != '"') {
                if (currentChar() == '\0' || currentChar() == '\n') {
                    addError(startLine, startColumn, "literal string nao terminado");
                    return null;
                }
                builder.append(currentChar());
                advance();
            }
            builder.append('"');
            advance();
            return new Token(TokenType.STRING_LITERAL, builder.toString(), startLine, startColumn);
        }

        private void addError(int line, int column, String message) {
            errors.add(new LexicalError(line, column, message));
        }

        private boolean isAsciiLetter(char value) {
            return (value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z');
        }

        private char currentChar() {
            if (index >= input.length()) {
                return '\0';
            }
            return input.charAt(index);
        }

        private char peekChar() {
            if (index + 1 >= input.length()) {
                return '\0';
            }
            return input.charAt(index + 1);
        }

        private void advance() {
            if (index >= input.length()) {
                return;
            }
            char value = input.charAt(index++);
            if (value == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
        }

        private String printable(char value) {
            if (value == '\0') {
                return "EOF";
            }
            if (value == '\n') {
                return "\\n";
            }
            if (value == '\t') {
                return "\\t";
            }
            return Character.toString(value);
        }
    }