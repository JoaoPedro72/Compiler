import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LexicalCompiler {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java LexicalCompiler.java <arquivo-fonte>");
            return;
        }

        Path sourcePath = Path.of(args[0]);
        String source = Files.readString(sourcePath, StandardCharsets.UTF_8);
        SymbolTable symbolTable = new SymbolTable();
        Lexer lexer = new Lexer(source, symbolTable);

        List<Token> tokens = new ArrayList<>();
        for (;;) {
            Token token = lexer.nextToken();
            tokens.add(token);
            if (token.type == TokenType.EOF) {
                break;
            }
        }

        System.out.println("TOKENS:");
        for (Token token : tokens) {
            System.out.println(token.format());
        }

        System.out.println();
        System.out.println("TABELA DE SIMBOLOS:");
        System.out.println(symbolTable.format());

        System.out.println();
        System.out.println("ERROS LEXICOS:");
        if (lexer.getErrors().isEmpty()) {
            System.out.println("Nenhum erro lexico encontrado.");
            System.out.println();
            System.out.println("RESULTADO: sucesso na analise lexica.");
            return;
        }

        for (LexicalError error : lexer.getErrors()) {
            System.out.println(error.format());
        }
        System.out.println();
        System.out.printf(
            "RESULTADO: %d erro(s) lexico(s) encontrado(s).%n",
            lexer.getErrors().size()
        );
        System.exit(1);
    }

    enum SymbolCategory {
        RESERVED_WORD,
        IDENTIFIER
    }

    enum TokenType {
        CLASS,
        INT,
        STRING,
        FLOAT,
        IF,
        ELSE,
        DO,
        WHILE,
        REPEAT,
        UNTIL,
        READ,
        WRITE,
        AND,
        OR,
        NOT,
        IDENTIFIER,
        INTEGER_CONST,
        REAL_CONST,
        STRING_LITERAL,
        ASSIGN,
        PLUS,
        MINUS,
        TIMES,
        DIVIDE,
        MOD,
        GT,
        GE,
        LT,
        LE,
        NE,
        EQ,
        LPAREN,
        RPAREN,
        LBRACE,
        RBRACE,
        COMMA,
        SEMICOLON,
        EOF
    }

    static final class Token {
        final TokenType type;
        final String lexeme;
        final int line;
        final int column;

        Token(TokenType type, String lexeme, int line, int column) {
            this.type = Objects.requireNonNull(type);
            this.lexeme = Objects.requireNonNull(lexeme);
            this.line = line;
            this.column = column;
        }

        String format() {
            return String.format("[%03d:%03d] %-14s %s", line, column, type.name(), quote(lexeme));
        }

        private static String quote(String text) {
            return "'" + text
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("'", "\\'") + "'";
        }
    }

    static final class LexicalError {
        final int line;
        final int column;
        final String message;

        LexicalError(int line, int column, String message) {
            this.line = line;
            this.column = column;
            this.message = message;
        }

        String format() {
            return String.format("linha %d, coluna %d - %s", line, column, message);
        }
    }

    static final class Symbol {
        final String lexeme;
        final SymbolCategory category;
        final TokenType tokenType;
        final int firstLine;

        Symbol(String lexeme, SymbolCategory category, TokenType tokenType, int firstLine) {
            this.lexeme = lexeme;
            this.category = category;
            this.tokenType = tokenType;
            this.firstLine = firstLine;
        }

        String format() {
            String lineText = firstLine == 0 ? "init" : Integer.toString(firstLine);
            return String.format("%-16s %-14s %-14s %s", lexeme, category.name(), tokenType.name(), lineText);
        }
    }

    static final class SymbolTable {
        private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();

        SymbolTable() {
            reserve("class", TokenType.CLASS);
            reserve("int", TokenType.INT);
            reserve("string", TokenType.STRING);
            reserve("float", TokenType.FLOAT);
            reserve("if", TokenType.IF);
            reserve("else", TokenType.ELSE);
            reserve("do", TokenType.DO);
            reserve("while", TokenType.WHILE);
            reserve("repeat", TokenType.REPEAT);
            reserve("until", TokenType.UNTIL);
            reserve("read", TokenType.READ);
            reserve("write", TokenType.WRITE);
            reserve("and", TokenType.AND);
            reserve("or", TokenType.OR);
            reserve("not", TokenType.NOT);
        }

        void reserve(String lexeme, TokenType type) {
            symbols.put(lexeme, new Symbol(lexeme, SymbolCategory.RESERVED_WORD, type, 0));
        }

        TokenType findReserved(String lexeme) {
            Symbol symbol = symbols.get(lexeme);
            if (symbol != null && symbol.category == SymbolCategory.RESERVED_WORD) {
                return symbol.tokenType;
            }
            return null;
        }

        void installIdentifier(String lexeme, int line) {
            symbols.computeIfAbsent(
                lexeme,
                ignored -> new Symbol(lexeme, SymbolCategory.IDENTIFIER, TokenType.IDENTIFIER, line)
            );
        }

        String format() {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("%-16s %-14s %-14s %s%n", "LEXEMA", "CATEGORIA", "TOKEN", "PRIMEIRA_LINHA"));
            for (Map.Entry<String, Symbol> entry : symbols.entrySet()) {
                builder.append(entry.getValue().format()).append(System.lineSeparator());
            }
            return builder.toString().stripTrailing();
        }
    }

    static final class Lexer {
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
}