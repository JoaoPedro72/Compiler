package lexicocode;

public class LexicalError {
    public final int line;
    public final int column;
    public final String message;

    public LexicalError(int line, int column, String message) {
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public String format() {
        return String.format("linha %d, coluna %d - %s", line, column, message);
    }
}