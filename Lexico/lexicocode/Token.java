package lexicocode;

import java.util.Objects;

public class Token {
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