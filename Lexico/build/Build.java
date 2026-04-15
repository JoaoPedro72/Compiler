import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Build {
    public static void main(String[] args) throws Exception {
        Path source = Path.of("LexicalCompiler.java");
        Path classesDir = Path.of("build", "classes");
        Path jarPath = Path.of("dist", "lexical-compiler.jar");
        Files.createDirectories(classesDir);
        Files.createDirectories(jarPath.getParent());

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Compilador Java indisponivel.");
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            boolean ok = compiler.getTask(
                null,
                fileManager,
                null,
                List.of("-d", classesDir.toString()),
                null,
                fileManager.getJavaFileObjectsFromPaths(List.of(source))
            ).call();
            if (!ok) {
                throw new IllegalStateException("Falha na compilacao do projeto Java.");
            }
        }

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "LexicalCompiler");

        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath), manifest)) {
            Files.walk(classesDir)
                .filter(Files::isRegularFile)
                .forEach(path -> addEntry(classesDir, path, jar));
        }

        System.out.println("Build concluido: " + jarPath);
    }

    private static void addEntry(Path baseDir, Path file, JarOutputStream jar) {
        String entryName = baseDir.relativize(file).toString().replace('\\', '/');
        try (InputStream input = Files.newInputStream(file)) {
            jar.putNextEntry(new JarEntry(entryName));
            input.transferTo(jar);
            jar.closeEntry();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}