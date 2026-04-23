package lexicocode;

/**
 * Representa um símbolo armazenado na tabela de símbolos.
 * 
 * Um símbolo pode ser uma palavra reservada ou um identificador,
 * contendo informações relevantes para o compilador.
 */
public class Symbol {
    public final String lexeme;          // Texto do símbolo
    public final SymbolCategory category; // Categoria (reservado ou identificador)
    public final TokenType tokenType;     // Tipo de token associado
    public final int firstLine;           // Linha da primeira ocorrência

    /**
     * Construtor da classe Symbol.
     * @param lexeme Texto do símbolo
     * @param category Categoria do símbolo (RESERVED_WORD ou IDENTIFIER)
     * @param tokenType Tipo de token associado
     * @param firstLine Linha da primeira ocorrência (0 para palavras reservadas)
     */
    public Symbol(String lexeme, SymbolCategory category, TokenType tokenType, int firstLine) {
        this.lexeme = lexeme;
        this.category = category;
        this.tokenType = tokenType;
        this.firstLine = firstLine;
    }

    /**
     * Retorna uma representação formatada do símbolo,
     * adequada para exibição em tabela.
     *
     * Regras:
     * - Se firstLine == 0 → exibe "init" (palavra reservada)
     * - Caso contrário → exibe o número da linha
     *
     * @return String formatada contendo:
     *         lexema, categoria, tipo de token e linha inicial
     */
    public String format() {
        String lineText = firstLine == 0 ? "init" : Integer.toString(firstLine);
        return String.format("%-16s %-14s %-14s %s", lexeme, category.name(), tokenType.name(), lineText);
    }
}