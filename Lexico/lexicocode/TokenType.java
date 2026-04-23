package lexicocode;

/**
 * Enumeração que define todos os tipos de tokens reconhecidos pelo analisador léxico.
 * 
 * Os tokens estão organizados por categorias:
 * - Palavras reservadas
 * - Identificadores e constantes
 * - Operadores
 * - Delimitadores
 * - Controle de fim de arquivo
 */
public enum TokenType {
    //Palavras reservadas da linguagem
    CLASS, INT, STRING, FLOAT,
    IF, ELSE, DO, WHILE, REPEAT, UNTIL,
    READ, WRITE, AND, OR, NOT,
    
    //Identificadores e constantes
    IDENTIFIER,        // Nome de variável, função, etc.
    INTEGER_CONST,     // Número inteiro (ex: 10)
    REAL_CONST,        // Número real (ex: 10.5)
    STRING_LITERAL,    // Texto entre aspas (ex: "hello")

    //Operadores
    ASSIGN,    // := 
    PLUS,      // +
    MINUS,     // -
    TIMES,     // *
    DIVIDE,    // /
    MOD,       // %

    GT,        // >
    GE,        // >=
    LT,        // <
    LE,        // <=
    NE,        // <>
    EQ,        // = ou ==

    //Delimitadores
    LPAREN,    // (
    RPAREN,    // )
    LBRACE,    // {
    RBRACE,    // }
    COMMA,     // ,
    SEMICOLON, // ;

    //Fim de arquivo
    EOF        // End Of File
}
