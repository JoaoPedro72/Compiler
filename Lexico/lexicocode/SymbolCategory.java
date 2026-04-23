package lexicocode;

/**
 * Enumeração que define as categorias possíveis de símbolos
 * armazenados na tabela de símbolos.
 * 
 * Essa classificação é utilizada para distinguir o papel
 * de cada lexema durante a análise léxica.
 */
public enum SymbolCategory {
    /**
     * Representa uma palavra reservada da linguagem.
     * 
     * Exemplos: class, if, while, int, etc.
     * Esses símbolos possuem significado fixo na linguagem
     * e não podem ser redefinidos pelo usuário.
     */
    RESERVED_WORD,
    /**
     * Representa um identificador definido pelo usuário.
     * 
     * Exemplos: nomes de variáveis, funções, classes, etc.
     * São inseridos dinamicamente na tabela de símbolos
     * durante a análise léxica.
     */
    IDENTIFIER
}