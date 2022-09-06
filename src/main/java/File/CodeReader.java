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

    public Line nextLine() {
        if (isEOF()) {
            return new Line(lineNumber, END_OF_FILE);
        }
        lineNumber++;
        return new Line(lineNumber, source.get(lineNumber-1));
    }

    private boolean isEOF() {
        return lineNumber >= source.size();
    }
}
