package lexicocode;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

    private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();

    public SymbolTable() {
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

    public void reserve(String lexeme, TokenType type) {
        symbols.put(lexeme, new Symbol(lexeme, SymbolCategory.RESERVED_WORD, type, 0));
    }

    public TokenType findReserved(String lexeme) {
        Symbol s = symbols.get(lexeme);
        if (s != null && s.category == SymbolCategory.RESERVED_WORD) {
            return s.tokenType;
        }
        return null;
    }

    public void installIdentifier(String lexeme, int line) {
        symbols.computeIfAbsent(
            lexeme,
            ignored -> new Symbol(lexeme, SymbolCategory.IDENTIFIER, TokenType.IDENTIFIER, line)
        );
    }

    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-16s %-14s %-14s %s%n", "LEXEMA", "CATEGORIA", "TOKEN", "PRIMEIRA_LINHA"));

        for (Map.Entry<String, Symbol> e : symbols.entrySet()) {
            sb.append(e.getValue().format()).append(System.lineSeparator());
        }

        return sb.toString().stripTrailing();
    }
}