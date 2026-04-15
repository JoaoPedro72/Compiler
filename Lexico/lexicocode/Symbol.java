package lexicocode;

public class Symbol {
    public final String lexeme;
    public final SymbolCategory category;
    public final TokenType tokenType;
    public final int firstLine;

    public Symbol(String lexeme, SymbolCategory category, TokenType tokenType, int firstLine) {
        this.lexeme = lexeme;
        this.category = category;
        this.tokenType = tokenType;
        this.firstLine = firstLine;
    }

    public String format() {
        String lineText = firstLine == 0 ? "init" : Integer.toString(firstLine);
        return String.format("%-16s %-14s %-14s %s", lexeme, category.name(), tokenType.name(), lineText);
    }
}