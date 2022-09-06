package main.java.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CodeReader {

    private final List<String> source;
    private int lineNumber = 0;

    public static final String END_OF_FILE = "EOF";

    public CodeReader(String sourcePath) {
        try {
            var sourceLines = Files.readAllLines(Path.of(sourcePath));
            source = sourceLines.stream()
                    .filter(line -> !isComment(line))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("Could not read file");
        }
    }

    private boolean isComment(String line) {
        return line.startsWith("//");
    }

    public String nextLine() {
        if (lineNumber < source.size()) {
            return source.get(lineNumber++);
        }
        return END_OF_FILE;
    }
}
