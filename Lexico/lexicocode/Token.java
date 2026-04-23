package lexicocode;

import java.util.Objects;

/**
 * Representa um token gerado pelo analisador léxico.
 * Um token contém seu tipo, o lexema correspondente e a posição no código fonte.
 */
public class Token {
    final TokenType type;
    final String lexeme;
    final int line;
    final int column;

    /**
     * Construtor da classe Token.
     *
     * @param type Tipo do token (não pode ser nulo)
     * @param lexeme Texto correspondente ao token (não pode ser nulo)
     * @param line Linha onde o token inicia
     * @param column Coluna onde o token inicia
     * @throws NullPointerException se type ou lexeme forem nulos
     */
    Token(TokenType type, String lexeme, int line, int column) {
        this.type = Objects.requireNonNull(type);
        this.lexeme = Objects.requireNonNull(lexeme);
        this.line = line;
        this.column = column;
    }

    /**
     * Retorna uma representação formatada do token.
     * O formato inclui linha, coluna, tipo e lexema com escape.
     *
     * Exemplo:
     * [001:005] IDENTIFIER    'variavel'
     *
     * @return String formatada representando o token
     */
    String format() {
        return String.format("[%03d:%03d] %-14s %s", line, column, type.name(), quote(lexeme));
    }

    /**
     * Escapa caracteres especiais do lexema e adiciona aspas simples.
     *
     * Caracteres tratados:
     * - '\'  → '\\'
     * - '\n' → '\\n'
     * - '\t' → '\\t'
     * - '\'' → '\\''
     *
     * @param text Texto a ser escapado
     * @return String com caracteres escapados e delimitada por aspas simples
     */
    private static String quote(String text) {
        return "'" + text
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("'", "\\'") + "'";
    }
}