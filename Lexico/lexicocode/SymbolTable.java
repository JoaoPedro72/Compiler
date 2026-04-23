package lexicocode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa a tabela de símbolos utilizada pelo analisador léxico.
 * Armazena palavras reservadas e identificadores encontrados no código.
 */
public class SymbolTable {

    // Estrutura que mantém os símbolos na ordem de inserção
    private final LinkedHashMap<String, Symbol> symbols = new LinkedHashMap<>();

    /**
     * Construtor da tabela de símbolos.
     * Inicializa automaticamente com as palavras reservadas da linguagem.
     */
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

    /**
     * Adiciona uma palavra reservada à tabela de símbolos.
     *
     * @param lexema Texto da palavra reservada
     * @param tipo Tipo de token correspondente
     */
    public void reserve(String lexema, TokenType tipo) {
        symbols.put(lexema, new Symbol(lexema, SymbolCategory.RESERVED_WORD, tipo, 0));
    }

    /**
     * Verifica se um lexema é uma palavra reservada.
     *
     * @param lexema Texto a ser verificado
     * @return Tipo do token se for palavra reservada, ou null caso contrário
     */
    public TokenType findReserved(String lexema) {
        Symbol s = symbols.get(lexema);
        if (s != null && s.category == SymbolCategory.RESERVED_WORD) {
            return s.tokenType;
        }
        return null;
    }

    /**
     * Instala um identificador na tabela de símbolos.
     * Caso já exista, não sobrescreve.
     *
     * @param lexema Nome do identificador
     * @param linha Linha onde o identificador foi encontrado pela primeira vez
     */
    public void installIdentifier(String lexema, int linha) {
        if (!symbols.containsKey(lexema)) {
            Symbol s = new Symbol(lexema, SymbolCategory.IDENTIFIER, TokenType.IDENTIFIER, linha);
            symbols.put(lexema, s);
        }
    }

    /**
     * Gera uma representação formatada da tabela de símbolos.
     *
     * @return String contendo a tabela formatada com colunas:
     *         LEXEMA, CATEGORIA, TOKEN e PRIMEIRA_LINHA
     */
    public String format() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-16s %-14s %-14s %s%n", "LEXEMA", "CATEGORIA", "TOKEN", "PRIMEIRA_LINHA"));

        for (Map.Entry<String, Symbol> e : symbols.entrySet()) {
            sb.append(e.getValue().format()).append(System.lineSeparator());
        }

        return sb.toString().stripTrailing();
    }
}
