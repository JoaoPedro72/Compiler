package lexicocode;

/**
 * Representa um erro léxico identificado durante a análise do código fonte.
 * Contém informações sobre a localização do erro e uma mensagem descritiva.
 */
public class LexicalError {
    public final int line;
    public final int column;
    public final String message;

    /**
     * Construtor da classe LexicalError.
     * @param line Linha onde o erro foi detectado
     * @param column Coluna onde o erro foi detectado
     * @param message Mensagem descritiva do erro
     */
    public LexicalError(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    /**
     * Retorna uma representação formatada do erro léxico.
     * Exemplo:
     * linha 3, coluna 15 - caractere invalido '@'
     * @return String formatada contendo linha, coluna e mensagem do erro
     */
    public String format() {
        return String.format("linha %d, coluna %d - %s", line, column, message);
    }
}