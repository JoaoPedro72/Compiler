package lexicocode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal responsável por executar o analisador léxico.
 * 
 * Fluxo de execução:
 * 1. Lê o arquivo fonte informado como argumento
 * 2. Inicializa a tabela de símbolos e o lexer
 * 3. Gera a lista de tokens
 * 4. Exibe:
 *    - Tokens encontrados
 *    - Tabela de símbolos
 *    - Erros léxicos (se houver)
 * 
 * Caso existam erros léxicos, o programa finaliza com código de erro (exit 1).
 */
public class LexicalCompiler {
    /**
     * Método principal da aplicação.
     *
     * @param args Argumentos da linha de comando.
     *             Espera-se:
     *             args[0] → caminho do arquivo fonte a ser analisado
     * 
     * @throws Exception Caso ocorra erro na leitura do arquivo
     */
    public static void main(String[] args) throws Exception {
        // Verifica se o arquivo foi informado
        if (args.length == 0) {
            System.out.println("Uso: java LexicalCompiler.java <arquivo-fonte>");
            return;
        }

        // Lê o arquivo fonte
        Path sourcePath = Path.of(args[0]);
        String source = Files.readString(sourcePath, StandardCharsets.UTF_8);

        // Inicializa componentes do compilador
        SymbolTable symbolTable = new SymbolTable();
        Lexer lexer = new Lexer(source, symbolTable);

        // Lista de tokens gerados
        List<Token> tokens = new ArrayList<>();

        /**
         * Processo de análise léxica:
         * Itera até encontrar EOF
         */
        for (;;) {
            Token token = lexer.nextToken();
            tokens.add(token);
            if (token.type == TokenType.EOF) {
                break;
            }
        }

        
        //Impressão dos tokens
        System.out.println("TOKENS:");
        for (Token token : tokens) {
            System.out.println(token.format());
        }

        //Impressão da tabela de símbolos
        System.out.println();
        System.out.println("TABELA DE SIMBOLOS:");
        System.out.println(symbolTable.format());

        //Impressão dos erros léxicos
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
        
        // Finaliza com erro
        System.exit(1);
    }
}