package lexicocode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class LexicalCompiler {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java LexicalCompiler.java <arquivo-fonte>");
            return;
        }

        Path sourcePath = Path.of(args[0]);
        String source = Files.readString(sourcePath, StandardCharsets.UTF_8);
        SymbolTable symbolTable = new SymbolTable();
        Lexer lexer = new Lexer(source, symbolTable);

        List<Token> tokens = new ArrayList<>();
        for (;;) {
            Token token = lexer.nextToken();
            tokens.add(token);
            if (token.type == TokenType.EOF) {
                break;
            }
        }

        System.out.println("TOKENS:");
        for (Token token : tokens) {
            System.out.println(token.format());
        }

        System.out.println();
        System.out.println("TABELA DE SIMBOLOS:");
        System.out.println(symbolTable.format());

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
        System.exit(1);
    }
}